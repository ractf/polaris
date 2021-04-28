package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.state.ClusterState;

public class InstancesGauge implements Gauge<Integer> {

    private final ClusterState clusterState;

    public InstancesGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Integer getValue() {
        return clusterState.getInstanceIds().size();
    }
}
