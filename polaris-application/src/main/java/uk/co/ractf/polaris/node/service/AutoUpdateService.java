package uk.co.ractf.polaris.node.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.co.ractf.polaris.node.runner.Runner;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Singleton
public class AutoUpdateService extends AbstractScheduledService {

    private final ClusterState clusterState;
    private final Set<Runner<?>> runners;

    @Inject
    public AutoUpdateService(final ClusterState clusterState, final Set<Runner<?>> runners) {
        this.clusterState = clusterState;
        this.runners = runners;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void runOneIteration() throws Exception {
        try {
            System.out.println("AutoUpdateService.runOneIteration");
            for (final var task : clusterState.getTasks().values()) {
                System.out.println(task.getId());
                for (final var pod : task.getPods()) {
                    System.out.println(pod.getId());
                    for (final Runner runner : runners) {
                        if (runner.getType() == pod.getClass()) {
                            runner.updatePod(task, pod);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(20, 20, TimeUnit.SECONDS);
    }
}
