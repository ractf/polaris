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
import uk.co.ractf.polaris.api.notification.NotificationTarget;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.node.Node;
import uk.co.ractf.polaris.node.NodeConfiguration;
import uk.co.ractf.polaris.node.runner.Runner;
import uk.co.ractf.polaris.notification.NotificationFacade;
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
    private final NotificationFacade notifications;

    private final LoadingCache<String, String> recentlyStartedInstances = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
                @Override
                public String load(final @NotNull String s) {
                    return s;
                }
            });

    private final Set<Runner<?>> runnerSet;

    @Inject
    public InstanceReconciliationService(final ClusterState state, final Node node, final Set<Runner<?>> runners,
                                         final NodeConfiguration nodeConfiguration,
                                         final NotificationFacade notifications) {
        this.state = state;
        this.node = node;
        this.runnerSet = runners;
        this.nodeConfiguration = nodeConfiguration;
        this.notifications = notifications;
    }

    @Override
    protected void startUp() throws Exception {
        for (final var runner : runnerSet) {
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
        final var start = System.currentTimeMillis();
        try {
            final var tasks = state.getTasks();
            for (final var entry : state.getInstancesOnNode(node.getId()).entrySet()) {
                final var instance = entry.getValue();
                final var task = tasks.get(entry.getValue().getTaskId());
                if (task == null) {
                    continue;
                }
                if (recentlyStartedInstances.getIfPresent(instance.getId()) != null) {
                    continue;
                }
                for (final var pod : task.getPods()) {
                    if (!getRunner(pod).canStartPod(pod)) {
                        log.info("Pulling image for {}/{}...", task.getId(), pod.getId());
                        getRunner(pod).preparePod(task, pod);
                    }
                }
                for (final var pod : task.getPods()) {
                    log.info("Creating pod {} from task {} and instance {}...", pod.getId(), task.getId(), instance.getId());
                    getRunner(pod).createPod(task, pod, instance);
                }
                recentlyStartedInstances.put(instance.getId(), "");
                if (task.getPods().size() > 1) {
                    /* TODO: This is assuming theres only one runner, thats a safe assumption now, the code shouldn't
                     *  be written assuming it is. */
                    log.info("Networking pods from task {} and instance {}...", task.getId(), instance.getId());
                    getRunner(task.getPods().get(0)).createNetwork(task.getPods(), task, instance);
                }
                for (final var pod : task.getPods()) {
                    CompletableFuture.runAsync(() -> {
                        if (!getRunner(pod).isPodStarted(pod, instance)) {
                            log.info("telling runner to start instance of {} for {}", pod.getId(), instance.getId());
                            getRunner(pod).startPod(task, pod, instance);
                        }
                    });
                }
            }
        } catch (final Exception exception) {
            log.error("Error reconciling instances", exception);
            notifications.error(NotificationTarget.SYSTEM_ADMIN, null,
                    "Error reconciling instance (node: " + node.getId() + ")", exception.getMessage());
        }
        final var end = System.currentTimeMillis();
        log.info("Finished instance reconcillation in {}ms", end - start);
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, nodeConfiguration.getReconciliationTickFrequency(), TimeUnit.MILLISECONDS);
    }

}
