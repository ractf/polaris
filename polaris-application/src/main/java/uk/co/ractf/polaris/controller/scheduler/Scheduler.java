package uk.co.ractf.polaris.controller.scheduler;

import com.google.inject.ImplementedBy;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;

/**
 * The scheduler dictates which node(s) will run a given task, the scheduler doesn't control replication, it just
 * gets asked to give a list of nodes to run N instances of the task.
 */
@ImplementedBy(GenericScheduler.class)
public interface Scheduler {

    /**
     * Schedule a {@link Task} onto the cluster
     *
     * @param task the task to schedule
     * @return the node to run the task on
     */
    ScheduleResult schedule(final Task task);

}
