package uk.co.ractf.polaris.node;

import com.github.dockerjava.api.model.AuthConfig;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.node.runner.Runner;
import uk.co.ractf.polaris.node.service.NodeServices;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Singleton
public class ConsulNode implements Node, Managed {

    private static final Logger log = LoggerFactory.getLogger(ConsulNode.class);

    private final String id;
    private final Set<Runner<?>> runnerSet;
    private final Set<Service> services;
    private final Map<Class<? extends Pod>, Runner<? extends Pod>> runners = new HashMap<>();
    private final ClusterState clusterState;

    @Inject
    public ConsulNode(final NodeConfiguration configuration, final Set<Runner<?>> runnerSet,
                      @NodeServices final Set<Service> services, final ClusterState clusterState) {
        this.clusterState = clusterState;
        this.id = "node"; //TODO
        this.runnerSet = runnerSet;
        this.services = services;
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
        final Challenge challenge = clusterState.getChallenge(instance.getChallengeId());
        if (challenge == null) {
            throw new IllegalStateException("Instance " + instance.getId() + " is of challenge " +
                    instance.getChallengeId() + " which does not exist in the state");
        }
        for (final Pod pod : challenge.getPods()) {
            CompletableFuture.runAsync(() -> getRunner(pod).restartPod(pod, instance));
        }
    }

    @Override
    public AuthConfig getAuthConfig() {
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T extends Pod> Runner<Pod> getRunner(final T pod) {
        return (Runner<Pod>) runners.get(pod.getClass());
    }

}
