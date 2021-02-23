package uk.co.ractf.polaris.host;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.InternetProtocol;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.management.OperatingSystemMXBean;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.util.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.PolarisConfiguration;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.host.HostInfo;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.api.pod.PortMapping;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.host.service.HostServices;
import uk.co.ractf.polaris.runner.Runner;
import java.util.*;
import java.util.concurrent.*;

@Singleton
public class EmbeddedHost implements Host, Managed {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedHost.class);

    private final Controller controller;
    private final Map<Class<? extends Pod>, Runner<? extends Pod>> runners = new HashMap<>();

    private final Map<String, Instance> instances = new ConcurrentHashMap<>();
    private final Set<Integer> ports = new ConcurrentSkipListSet<>();
    private final Set<Runner> runnerSet;
    private final Set<Service> services;

    private final int advertisedMinPort;
    private final int advertisedMaxPort;
    private final int unadvertisedMinPort;
    private final int unadvertisedMaxPort;

    private HostInfo hostInfo;

    @Inject
    public EmbeddedHost(final Controller controller,
                        final PolarisConfiguration polarisConfiguration,
                        final Set<Runner> runnerSet,
                        @HostServices final Set<Service> services) {
        this.controller = controller;
        this.runnerSet = runnerSet;
        this.services = services;

        this.advertisedMinPort = polarisConfiguration.getAdvertisedMinPort();
        this.advertisedMaxPort = polarisConfiguration.getAdvertisedMaxPort();
        this.unadvertisedMinPort = polarisConfiguration.getUnadvertisedMinPort();
        this.unadvertisedMaxPort = polarisConfiguration.getUnadvertisedMaxPort();

        controller.addHost(this);
    }

    @Override
    public void start() {
        for (final Runner<?> runner : runnerSet) {
            this.runners.put(runner.getType(), runner);
        }
        for (final Service service : services) {
            service.startAsync();
        }
    }

    @Override
    public void stop() {
        for (final Service service : services) {
            service.stopAsync();
        }
    }

    @Override
    public String getID() {
        return "embedded";
    }

    @Override
    public HostInfo getHostInfo() {
        return hostInfo;
    }

    @Override
    public void setHostInfo(final HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }

    @Override
    public Instance createInstance(final Challenge challenge, final Deployment deployment) {
        log.debug("Instance for {} created", challenge.getID());
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
        if (challenge == null) {
            return;
        }
        for (final Pod pod : challenge.getPods()) {
            CompletableFuture.runAsync(() -> getRunner(pod).restartPod(pod, instance));
        }
    }

    @Override
    public Instance getInstance(final String id) {
        return instances.get(id);
    }

    private int generatePort(final int min, final int max) {
        int port;
        do {
            port = ThreadLocalRandom.current().nextInt(min, max);
        } while (ports.contains(port));
        return port;
    }

    @Override
    public Map<PortMapping, PortBinding> createPortBindings(final List<PortMapping> portMappings) {
        final Map<PortMapping, PortBinding> portBindings = new HashMap<>();
        int lastAdvertisedPort = generatePort(advertisedMinPort, advertisedMaxPort);
        int lastUnadvertisedPort = generatePort(unadvertisedMinPort, unadvertisedMaxPort);
        for (final PortMapping portMapping : portMappings) {
            int externalPort = portMapping.isAdvertise() ? lastAdvertisedPort : lastUnadvertisedPort;
            final InternetProtocol protocol = "udp".equalsIgnoreCase(portMapping.getProtocol()) ? InternetProtocol.UDP : InternetProtocol.TCP;
            portBindings.put(portMapping, new PortBinding(new Ports.Binding("0.0.0.0", externalPort + "/" + protocol.toString()),
                    new ExposedPort(portMapping.getPort(), protocol)));

            if (!ports.contains(externalPort + 1)) {
                if (portMapping.isAdvertise()) {
                    lastAdvertisedPort++;
                } else {
                    lastUnadvertisedPort++;
                }
            } else {
                if (portMapping.isAdvertise()) {
                    lastAdvertisedPort = generatePort(advertisedMinPort, advertisedMaxPort);
                } else {
                    lastUnadvertisedPort = generatePort(unadvertisedMinPort, unadvertisedMaxPort);
                }
            }
            ports.add(externalPort);
        }
        return portBindings;
    }

    @SuppressWarnings("unchecked")
    private <T extends Pod> Runner<Pod> getRunner(final T pod) {
        return (Runner<Pod>) runners.get(pod.getClass());
    }

}
