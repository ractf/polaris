package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Map;

public class PodsGauge implements Gauge<Integer> {

    private final ClusterState clusterState;

    public PodsGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Integer getValue() {
        var total = 0;
        for (final var entry : clusterState.getTasks().entrySet()) {
            total += entry.getValue().getPods().size();
        }

        return total;
    }
}
