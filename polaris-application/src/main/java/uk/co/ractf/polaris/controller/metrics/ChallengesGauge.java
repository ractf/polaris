package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import com.google.inject.Inject;
import uk.co.ractf.polaris.state.ClusterState;

public class ChallengesGauge implements Gauge<Integer> {

    private final ClusterState clusterState;

    @Inject
    public ChallengesGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Integer getValue() {
        return clusterState.getChallenges().size();
    }
}
