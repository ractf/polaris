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
        int total = 0;
        for (final String deployment : clusterState.getDeployments().keySet()) {
            total += clusterState.getInstancesForDeployment(deployment).size();
        }
        return total;
    }
}
