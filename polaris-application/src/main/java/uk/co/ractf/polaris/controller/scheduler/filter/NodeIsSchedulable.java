package uk.co.ractf.polaris.controller.scheduler.filter;

import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.scheduler.FilterPlugin;
import uk.co.ractf.polaris.controller.scheduler.FilterResult;

import java.util.Collections;

public class NodeIsSchedulable implements FilterPlugin {

    @Override
    public FilterResult filter(final Task task, final NodeInfo nodeInfo) {
        if (!nodeInfo.isSchedulable()) {
            return new FilterResult(false, false, Collections.singletonList("Node is not schedulable"));
        }
        return FilterResult.SCHEDULABLE;
    }

    @Override
    public String getName() {
        return "NodeIsSchedulable";
    }
}
