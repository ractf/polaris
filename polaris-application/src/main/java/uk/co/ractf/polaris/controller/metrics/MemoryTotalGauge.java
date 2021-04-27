package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Map;

public class MemoryTotalGauge implements Gauge<Long> {

    private final ClusterState clusterState;

    public MemoryTotalGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Long getValue() {
        long total = 0;
        for (final Map.Entry<String, NodeInfo> entry : clusterState.getNodes().entrySet()) {
            total += entry.getValue().getTotalMemory();
        }

        return total;
    }
}
