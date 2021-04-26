package uk.co.ractf.polaris.node.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.node.Node;
import uk.co.ractf.polaris.node.NodeConfiguration;
import uk.co.ractf.polaris.node.runner.Runner;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class InstanceReconciliationService extends AbstractScheduledService {

    private static final Logger log = LoggerFactory.getLogger(InstanceReconciliationService.class);

    private final ClusterState state;
    private final Node node;
    private final Map<Class<? extends Pod>, Runner<? extends Pod>> runners = new HashMap<>();
    private final NodeConfiguration nodeConfiguration;
    private final LoadingCache<String, String> recentlyStartedInstances = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
                @Override
                public String load(final @NotNull String s) {
                    return s;
                }
            });

    private final Set<Runner> runnerSet;

    @Inject
    public InstanceReconciliationService(final ClusterState state, final Node node, final Set<Runner> runners,
                                         final NodeConfiguration nodeConfiguration) {
        this.state = state;
        this.node = node;
        this.runnerSet = runners;
        this.nodeConfiguration = nodeConfiguration;
    }

    @Override
    protected void startUp() throws Exception {
        for (final Runner<?> runner : runnerSet) {
            this.runners.put(runner.getType(), runner);
        }
        super.startUp();
    }

    @SuppressWarnings("unchecked")
    private <T extends Pod> Runner<Pod> getRunner(final T pod) {
        return (Runner<Pod>) runners.get(pod.getClass());
    }

    @Override
    protected void runOneIteration() {
        try {
            for (final Map.Entry<String, Instance> entry : state.getInstancesOnNode(node.getId()).entrySet()) {
                final Instance instance = entry.getValue();
                final Challenge challenge = state.getChallengeFromDeployment(instance.getDeploymentId());
                if (challenge == null) {
                    continue;
                }
                for (final Pod pod : challenge.getPods()) {
                    if (recentlyStartedInstances.getIfPresent(pod.getId() + instance.getId()) != null) {
                        continue;
                    }
                    if (getRunner(pod).canStartPod(pod)) {
                        log.debug("ensuring pod started {} {}", pod.getId(), instance.getId());
                        recentlyStartedInstances.put(pod.getId() + instance.getId(), "");
                        CompletableFuture.runAsync(() -> {
                            if (!getRunner(pod).isPodStarted(pod, instance)) {
                                log.info("telling runner to start instance of {} for {}", pod.getId(), instance.getId());
                                getRunner(pod).startPod(pod, instance);
                            }
                        });
                    } else {
                        getRunner(pod).preparePod(pod);
                    }
                }
            }
        } catch (final Exception exception) {
            log.error("error", exception);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, nodeConfiguration.getReconciliationTickFrequency(), TimeUnit.MILLISECONDS);
    }

}
