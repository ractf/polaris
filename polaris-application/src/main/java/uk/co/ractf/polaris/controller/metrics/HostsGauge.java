package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import com.google.inject.Inject;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.state.ClusterState;

public class HostsGauge implements Gauge<Integer> {

    private final ClusterState clusterState;

    @Inject
    public HostsGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Integer getValue() {
        return clusterState.getNodes().size();
    }
}
