package uk.co.ractf.polaris.controller.scheduler.filter;

import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.scheduler.FilterPlugin;
import uk.co.ractf.polaris.controller.scheduler.FilterResult;

import java.util.Collections;

public class NodeHasRunner implements FilterPlugin {

    @Override
    public FilterResult filter(final Task task, final NodeInfo nodeInfo) {
        for (final var pod : task.getPods()) {
            var runnable = false;
            for (final var runner : nodeInfo.getRunners()) {
                if (pod.canUseRunner(runner)) {
                    runnable = true;
                    break;
                }
            }
            if (!runnable) {
                return new FilterResult(false, false, Collections.singletonList("No runner available for pod " + pod.getId()));
            }
        }
        return FilterResult.SCHEDULABLE;
    }

    @Override
    public String getName() {
        return "NodeHasRunner";
    }
}
