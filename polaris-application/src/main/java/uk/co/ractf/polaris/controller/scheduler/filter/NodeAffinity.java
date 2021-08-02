package uk.co.ractf.polaris.controller.scheduler.filter;

import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.pod.PodWithAffinity;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.scheduler.FilterPlugin;
import uk.co.ractf.polaris.controller.scheduler.FilterResult;

import java.util.Collections;

public class NodeAffinity implements FilterPlugin {

    @Override
    public FilterResult filter(final Task task, final NodeInfo nodeInfo) {
        for (final var pod : task.getPods()) {
            if (pod instanceof PodWithAffinity) {
                final var affinity = ((PodWithAffinity) pod).getAffinity();
                if (affinity == null) {
                    return FilterResult.SCHEDULABLE;
                }
                for (final var entry : affinity.entrySet()) {
                    final var value = nodeInfo.getLabels().get(entry.getKey());
                    if (!entry.getValue().equals(value) && !entry.getValue().isEmpty()) {
                        return new FilterResult(false, false, Collections.singletonList("Node does not meet affinity for pod " + pod.getId()));
                    }
                }
            }
        }
        return FilterResult.SCHEDULABLE;
    }

    @Override
    public String getName() {
        return "NodeAffinity";
    }
}
