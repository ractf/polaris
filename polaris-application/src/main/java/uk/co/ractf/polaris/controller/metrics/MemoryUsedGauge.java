package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.state.ClusterState;

public class MemoryUsedGauge implements Gauge<Long> {

    private final ClusterState clusterState;

    public MemoryUsedGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Long getValue() {
        long total = 0;
        for (final var entry : clusterState.getNodes().entrySet()) {
            total += entry.getValue().getTotalMemory() - entry.getValue().getFreeMemory();
        }

        return total;
    }
}
