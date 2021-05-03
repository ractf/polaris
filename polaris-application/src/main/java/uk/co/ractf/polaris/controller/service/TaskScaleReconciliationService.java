package uk.co.ractf.polaris.controller.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.annotation.ExcludeFromGeneratedTestReport;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.controller.scheduler.TaskScaler;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.concurrent.TimeUnit;

@Singleton
public class TaskScaleReconciliationService extends AbstractScheduledService {

    private static final Logger log = LoggerFactory.getLogger(TaskScaleReconciliationService.class);

    private final ClusterState clusterState;
    private final TaskScaler taskScaler;
    private final ControllerConfiguration config;

    @Inject
    public TaskScaleReconciliationService(final ClusterState controller, final TaskScaler taskScaler,
                                          final ControllerConfiguration config) {
        this.clusterState = controller;
        this.taskScaler = taskScaler;
        this.config = config;
    }

    @Override
    protected void runOneIteration() {
        try {
            final var tasks = clusterState.getTasks();
            log.trace("Starting task scale reconciliation tick");

            final var nodes = clusterState.getNodes().values();
            if (nodes.size() == 0) {
                log.debug("No nodes detected, skipping reconciliation");
                return;
            }

            for (final var task : tasks.values()) {
                taskScaler.scaleTask(task);
            }
        } catch (final Exception e) {
            log.error("Error reconciling deployments", e);
        }
    }

    @Override
    @ExcludeFromGeneratedTestReport
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(config.getReconciliationTickFrequency(), config.getReconciliationTickFrequency(), TimeUnit.MILLISECONDS);
    }
}
