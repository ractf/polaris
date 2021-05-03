package uk.co.ractf.polaris.controller.scheduler;

import uk.co.ractf.polaris.api.node.NodeInfo;

import java.util.List;

/**
 * The result of a single {@link uk.co.ractf.polaris.api.task.Task} being scheduled.
 */
public class ScheduleResult {

    private final NodeInfo node;
    private final boolean successful;
    private final int evaluatedNodes;
    private final int eligibleNodes;
    private final List<String> failureReason;

    public ScheduleResult(final NodeInfo node, final boolean successful, final int evaluatedNodes, final int eligibleNodes,
                          final List<String> failureReason) {
        this.node = node;
        this.successful = successful;
        this.evaluatedNodes = evaluatedNodes;
        this.eligibleNodes = eligibleNodes;
        this.failureReason = failureReason;
    }

    public NodeInfo getNode() {
        return node;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public int getEvaluatedNodes() {
        return evaluatedNodes;
    }

    public int getEligibleNodes() {
        return eligibleNodes;
    }

    public List<String> getFailureReason() {
        return failureReason;
    }
}
