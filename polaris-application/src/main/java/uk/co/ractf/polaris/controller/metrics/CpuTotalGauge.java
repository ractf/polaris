package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Map;

public class CpuTotalGauge implements Gauge<Double> {

    private final ClusterState clusterState;

    public CpuTotalGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Double getValue() {
        double total = 0;
        for (final var entry : clusterState.getNodes().entrySet()) {
            total += entry.getValue().getProcessors();
        }

        return total;
    }
}