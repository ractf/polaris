package uk.co.ractf.polaris.node;

import com.github.dockerjava.api.model.*;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.api.pod.PortMapping;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.node.runner.Runner;
import uk.co.ractf.polaris.node.service.NodeServices;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@Deprecated
public class EmbeddedNode implements Node, Managed {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedNode.class);

    private final Controller controller;
    private final NodeConfiguration configuration;
    private final Map<Class<? extends Pod>, Runner<? extends Pod>> runners = new HashMap<>();

    private final Map<String, Instance> instances = new ConcurrentHashMap<>();
    private final Set<Integer> ports = new ConcurrentSkipListSet<>();
    private final Set<Runner> runnerSet;
    private final Set<Service> services;
    private final ClusterState clusterState;

    private final int advertisedMinPort;
    private final int advertisedMaxPort;
    private final int unadvertisedMinPort;
    private final int unadvertisedMaxPort;

    private NodeInfo nodeInfo;

    @Inject
    public EmbeddedNode(final Controller controller,
                        final NodeConfiguration configuration,
                        final Set<Runner> runnerSet,
                        @NodeServices final Set<Service> services, final ClusterState clusterState) {
        this.controller = controller;
        this.configuration = configuration;
        this.runnerSet = runnerSet;
        this.services = services;

        this.advertisedMinPort = configuration.getAdvertisedMinPort();
        this.advertisedMaxPort = configuration.getAdvertisedMaxPort();
        this.unadvertisedMinPort = configuration.getUnadvertisedMinPort();
        this.unadvertisedMaxPort = configuration.getUnadvertisedMaxPort();
        this.clusterState = clusterState;
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
    public String getId() {
        return "embedded";
    }

    @Override
    public NodeInfo getHostInfo() {
        return nodeInfo;
    }

    @Override
    public void setHostInfo(final NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    @Override
    public void restartInstance(final Instance instance) {
        final Challenge challenge = clusterState.getChallenge(instance.getChallengeId());
        if (challenge == null) {
            return;
        }
        for (final Pod pod : challenge.getPods()) {
            CompletableFuture.runAsync(() -> getRunner(pod).restartPod(pod, instance));
        }
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
            final int externalPort = portMapping.isAdvertise() ? lastAdvertisedPort : lastUnadvertisedPort;
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

    @Override
    public AuthConfig getAuthConfig() {
        return new AuthConfig()
                .withUsername(configuration.getRegistryUsername())
                .withPassword(configuration.getRegistryPassword());
    }

    @SuppressWarnings("unchecked")
    private <T extends Pod> Runner<Pod> getRunner(final T pod) {
        return (Runner<Pod>) runners.get(pod.getClass());
    }

}
