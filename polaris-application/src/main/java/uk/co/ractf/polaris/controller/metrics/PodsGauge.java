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
        int total = 0;
        for (final Map.Entry<String, Challenge> entry : clusterState.getChallenges().entrySet()) {
            total += entry.getValue().getPods().size();
        }

        return total;
    }
}
