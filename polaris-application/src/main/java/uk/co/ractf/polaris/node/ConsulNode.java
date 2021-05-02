package uk.co.ractf.polaris.node;

import com.github.dockerjava.api.model.AuthConfig;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.node.runner.Runner;
import uk.co.ractf.polaris.node.service.NodeServices;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class ConsulNode implements Node, Managed {

    private static final Logger log = LoggerFactory.getLogger(ConsulNode.class);

    private final String id;
    private final Set<Runner<?>> runnerSet;
    private final Set<Service> services;
    private final Map<Class<? extends Pod>, Runner<? extends Pod>> runners = new HashMap<>();
    private final ClusterState clusterState;

    @Inject
    public ConsulNode(final Set<Runner<?>> runnerSet, @NodeServices final Set<Service> services,
                      final ClusterState clusterState) {
        this.clusterState = clusterState;
        this.id = "node"; //TODO
        this.runnerSet = runnerSet;
        this.services = services;
    }

    @Override
    public void start() {
        for (final var runner : runnerSet) {
            this.runners.put(runner.getType(), runner);
        }
        for (final var service : services) {
            service.startAsync();
        }
    }

    @Override
    public void stop() {
        for (final var service : services) {
            service.stopAsync();
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public NodeInfo getNodeInfo() {
        return clusterState.getNode(id);
    }

    @Override
    public void setNodeInfo(final NodeInfo nodeInfo) {
        clusterState.setNodeInfo(nodeInfo);
    }

    @Override
    public void restartInstance(final Instance instance) {

        final var task = clusterState.getTask(instance.getTaskId());
        if (task == null) {
            throw new IllegalStateException("Instance " + instance.getId() + " is of task " +
                    instance.getTaskId().toString() + " which does not exist in the state");
        }
        for (final var pod : task.getPods()) {
            CompletableFuture.runAsync(() -> getRunner(pod).restartPod(pod, instance));
        }
    }

    @Override
    public AuthConfig getAuthConfig() {
        return null;
    }

    @Override
    public List<String> getPodImages() {
        final List<String> images = new ArrayList<>();
        for (final var runner : runners.values()) {
            images.addAll(runner.getImages());
        }
        return images;
    }

    @Override
    public List<String> getRunners() {
        return runners.values().stream().map(Runner::getName).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private <T extends Pod> Runner<Pod> getRunner(final T pod) {
        return (Runner<Pod>) runners.get(pod.getClass());
    }

}
