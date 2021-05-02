package uk.co.ractf.polaris.controller.scheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a filter plugin, if a node can be scheduled onto, and if not, can it be resolved and reasons
 * why.
 */
public class FilterResult {

    public static FilterResult SCHEDULABLE = new FilterResult(true, false, new ArrayList<>());

    private final boolean schedulable;
    private final boolean resolvable;
    private final List<String> reason;

    /**
     * @param schedulable is the node schedulable
     * @param resolvable  can the node be made schedulable
     * @param reason      why is the node not schedulable
     */
    public FilterResult(final boolean schedulable, final boolean resolvable, final List<String> reason) {
        this.schedulable = schedulable;
        this.resolvable = resolvable;
        this.reason = reason;
    }

    public boolean isSchedulable() {
        return schedulable;
    }

    public boolean isResolvable() {
        return resolvable;
    }

    public List<String> getReason() {
        return reason;
    }

}
