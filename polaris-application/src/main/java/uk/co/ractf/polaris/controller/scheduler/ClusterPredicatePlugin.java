package uk.co.ractf.polaris.controller.scheduler;

import uk.co.ractf.polaris.api.task.Task;

/**
 * A {@link SchedulingAlgorithm} {@link Plugin} that checks that a {@link Task} can actually run on the polaris cluster. Scheduling
 * will fail if any of these plugins return false.
 */
public interface ClusterPredicatePlugin extends Plugin {

    /**
     * Returns if it is possible to schedule this task.
     *
     * @param task the task
     * @return if the task can run and why
     */
    ClusterPredicateResult canSchedule(final Task task);

}
