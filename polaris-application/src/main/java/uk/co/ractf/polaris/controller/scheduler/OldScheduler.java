package uk.co.ractf.polaris.controller.scheduler;

import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.node.Node;

import java.util.Collection;

/**
 * Interface to schedule {@link Task}s onto {@link Node}s
 */
public interface OldScheduler {

    /**
     * Return the best {@link Node} for this {@link Task} to run on
     *
     * @param task  the task
     * @param nodes the nodes
     * @return the host it should be scheduled on
     */
    NodeInfo scheduleTask(final Task challenge, final Collection<NodeInfo> nodes);

    /**
     * Returns the best {@link Instance} to be descheduled out of the currently scheduled set
     *
     * @param task      the {@link Task}
     * @return the instance to deschedule
     */
    Instance descheduleInstance(final Task challenge, final Collection<NodeInfo> nodes, final Collection<Instance> instances);

}
