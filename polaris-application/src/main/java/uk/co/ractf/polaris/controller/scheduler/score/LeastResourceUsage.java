package uk.co.ractf.polaris.controller.scheduler.score;

import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.scheduler.ScorePlugin;

public class LeastResourceUsage implements ScorePlugin {

    @Override
    public double score(final Task task, final NodeInfo nodeInfo) {

        return (1 - ((float)nodeInfo.getFreeMemory() / nodeInfo.getTotalMemory())) *
                (1 - ((float)(nodeInfo.getProcessors() - nodeInfo.getCpuLoad()) / nodeInfo.getProcessors()));
    }

    @Override
    public int getWeight() {
        return 1;
    }
}
