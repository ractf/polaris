package uk.co.ractf.polaris.controller.scheduler;

import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;

/**
 * A {@link SchedulingAlgorithm} {@link Plugin} that scores how well a {@link Task} can run on a given {@link NodeInfo}.
 */
public interface ScorePlugin {

    /**
     * Scores how well a {@link Task} can run on a given {@link NodeInfo}. The returned double has no bounds but should
     * be normalised across all nodes to be between 0 and 1.
     *
     * @param task the task
     * @param nodeInfo the node to score
     * @return the score
     */
    double score(final Task task, final NodeInfo nodeInfo);

    /**
     * How heavily should this scoring plugin be considered in the scheduling algorithm
     *
     * @return the weight
     */
    int getWeight();

}
