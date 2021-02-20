package uk.co.ractf.polaris.host;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.InternetProtocol;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.api.pod.PortMapping;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.runner.DockerRunner;
import uk.co.ractf.polaris.runner.Runner;

import java.util.*;
import java.util.concurrent.*;

public class EmbeddedHost implements Host {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedHost.class);

    private final Controller controller;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ExecutorService executorService;
    private final Map<Class<? extends Pod>, Runner<? extends Pod>> runners = new HashMap<>();

    private final Map<String, Instance> instances = new ConcurrentHashMap<>();
    private final Set<Integer> ports = new ConcurrentSkipListSet<>();
    private final LoadingCache<String, String> recentlyStartedInstances = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
                @Override
                public String load(final String s) {
                    return s;
                }
            });

    public EmbeddedHost(final Controller controller, final DockerClient dockerClient,
                        final ScheduledExecutorService scheduledExecutorService, final ExecutorService executorService) {
        this.controller = controller;
        this.scheduledExecutorService = scheduledExecutorService;
        this.executorService = executorService;

        this.scheduledExecutorService.scheduleAtFixedRate(this::garbageCollectContainers, 60, 60, TimeUnit.MINUTES);
        this.scheduledExecutorService.scheduleAtFixedRate(this::reconciliationTick, 2, 5, TimeUnit.SECONDS);

        registerRunner(new DockerRunner(executorService, dockerClient, controller, this));
    }

    private void registerRunner(final Runner<?> runner) {
        runners.put(runner.getType(), runner);
    }

    @SuppressWarnings("unchecked")
    private <T extends Pod> Runner<Pod> getRunner(final T pod) {
        return (Runner<Pod>) runners.get(pod.getClass());
    }

    @Override
    public String getID() {
        return "embedded";
    }

    @Override
    public Instance createInstance(final Challenge challenge, final Deployment deployment) {
        log.info("Instance for {} created", challenge.getID());
        final Instance instance = new Instance(UUID.randomUUID().toString(), deployment.getID(), challenge.getID(), getID());
        instances.put(instance.getID(), instance);
        return instance;
    }

    @Override
    public void removeInstance(final Instance instance) {
        instances.remove(instance.getID());
    }

    @Override
    public Map<String, Instance> getInstances() {
        return Collections.unmodifiableMap(instances);
    }

    @Override
    public void restartInstance(final Instance instance) {
        final Challenge challenge = controller.getChallenge(instance.getChallengeID());
        for (final Pod pod : challenge.getPods()) {
            executorService.submit(() -> getRunner(pod).restartPod(pod, instance));
        }
    }

    @Override
    public Instance getInstance(final String id) {
        return instances.get(id);
    }

    @Override
    public Map<PortMapping, PortBinding> createPortBindings(final List<PortMapping> portMappings) {
        final Map<PortMapping, PortBinding> portBindings = new HashMap<>();
        int externalPort = generatePort();
        for (final PortMapping portMapping : portMappings) {
            final InternetProtocol protocol = "udp".equalsIgnoreCase(portMapping.getProtocol()) ? InternetProtocol.UDP : InternetProtocol.TCP;
            portBindings.put(portMapping, new PortBinding(new Ports.Binding("0.0.0.0", externalPort + "/" + protocol.toString()),
                    new ExposedPort(portMapping.getPort(), protocol)));
            if (!ports.contains(externalPort + 1) && externalPort + 1 < 65536) {
                externalPort++;
            } else {
                externalPort = generatePort();
            }
        }
        return portBindings;
    }

    @Override
    public String getPublicIp() {
        return "127.0.0.1";
    }

    private void garbageCollectContainers() {
        for (final Runner<? extends Pod> runner : runners.values()) {
            runner.garbageCollect();
        }
    }

    private void reconciliationTick() {
        try {
            log.debug("Running host reconciliation tick");
            for (final Map.Entry<String, Instance> entry : instances.entrySet()) {
                final Instance instance = entry.getValue();
                final Challenge challenge = controller.getChallengeFromDeployment(instance.getDeploymentID());
                for (final Pod pod : challenge.getPods()) {
                    if (recentlyStartedInstances.getIfPresent(pod.getID() + instance.getID()) != null) {
                        continue;
                    }
                    if (getRunner(pod).canStartPod(pod)) {
                        recentlyStartedInstances.put(pod.getID() + instance.getID(), "");
                        executorService.submit(() -> {
                            try {
                                ensurePodStarted(pod, instance);
                            } catch (InterruptedException e) {
                                log.error("Error when starting pod", e);
                            }
                        });
                    } else {
                        getRunner(pod).preparePod(pod);
                    }
                }
            }


            for (final Runner<? extends Pod> runner : runners.values()) {
                runner.killOrphans();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensurePodStarted(final Pod pod, final Instance instance) throws InterruptedException {
        if (!getRunner(pod).isPodStarted(pod, instance)) {
            getRunner(pod).startPod(pod, instance);
        }
    }

    private int generatePort() {
        int port;
        do {
            port = ThreadLocalRandom.current().nextInt(10000, 65535);
        } while (ports.contains(port));
        return port;
    }

}
