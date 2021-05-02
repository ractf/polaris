package uk.co.ractf.polaris.controller.scheduler;

import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.node.Node;

import java.util.Collection;

/**
 * Interface to schedule {@link Challenge}s onto {@link Node}s
 */
public interface Scheduler {

    /**
     * Return the best {@link Node} for this {@link Challenge} to run on out of the {@link Collection}
     *
     * @param challenge the challenge
     * @param nodes     the hosts
     * @return the host it should be scheduled on
     */
    NodeInfo scheduleTask(final Task task, final Collection<NodeInfo> nodes);

    /**
     * Returns the best {@link Instance} to be descheduled out of the current set
     *
     * @param challenge the {@link Challenge}
     * @param nodes     the clusters {@link Node}s
     * @param instances the instance
     * @return the instance to deschedule
     */
    Instance descheduleInstance(final Task task, final Collection<NodeInfo> nodes, final Collection<Instance> instances);

}
