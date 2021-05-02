package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.state.ClusterState;

public class CpuUsedGauge implements Gauge<Double> {

    private final ClusterState clusterState;

    public CpuUsedGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Double getValue() {
        double total = 0;
        for (final var entry : clusterState.getNodes().entrySet()) {
            total += entry.getValue().getCpuLoad();
        }

        return total;
    }
}
