package uk.co.ractf.polaris.controller.scheduler;

import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;

/**
 * A {@link SchedulingAlgorithm} {@link Plugin} that checks that a {@link Task} can run on a given node.
 */
public interface FilterPlugin extends Plugin {

    /**
     * Can the task run on the node?
     *
     * @param task     the task
     * @param nodeInfo the node
     * @return if the task can run
     */
    FilterResult filter(final Task task, final NodeInfo nodeInfo);

}
