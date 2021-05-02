package uk.co.ractf.polaris.controller.scheduler;

import java.util.List;

public class ClusterPredicateResult {

    public static final ClusterPredicateResult SCHEDULABLE = new ClusterPredicateResult(true, null);

    private final boolean possible;
    private final List<String> reason;

    public ClusterPredicateResult(final boolean possible, final List<String> reason) {
        this.possible = possible;
        this.reason = reason;
    }

    public boolean isPossible() {
        return possible;
    }

    public List<String> getReason() {
        return reason;
    }
}
