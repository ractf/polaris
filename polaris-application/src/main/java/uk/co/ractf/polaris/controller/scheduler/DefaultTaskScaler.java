package uk.co.ractf.polaris.controller.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.replication.ReplicationController;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.ArrayList;

@Singleton
public class DefaultTaskScaler implements TaskScaler {

    private static final Logger log = LoggerFactory.getLogger(DefaultTaskScaler.class);

    private final ClusterState clusterState;
    private final Scheduler scheduler;

    @Inject
    public DefaultTaskScaler(final ClusterState clusterState, final Scheduler scheduler) {
        this.clusterState = clusterState;
        this.scheduler = scheduler;
    }

    @Override
    public void scaleTask(final Task task) {
        log.debug("Attempting to scale task {}", task.getId());
        if (!clusterState.lockTask(task)) {
            log.debug("Could not obtain lock for {}", task.getId());
            return;
        }
        final var instances = new ArrayList<>(clusterState.getInstancesOfTask(task.getId()).values());

        final var scaleAmount = ReplicationController.create(task.getReplication()).getScaleAmount(instances, clusterState);
        log.debug("Scaling required for {}: {}", task.getId(), scaleAmount);

        if (scaleAmount > 0) {
            log.info("Scheduling instances: {} of {}", scaleAmount, task.getId());
            for (var i = 0; i < scaleAmount; i++) {
                scheduler.schedule(task);
            }
        } else {
            for (var i = 0; i > scaleAmount; i--) {
                scheduler.deschedule(task);
            }
        }
        clusterState.unlockTask(task);
    }
}
