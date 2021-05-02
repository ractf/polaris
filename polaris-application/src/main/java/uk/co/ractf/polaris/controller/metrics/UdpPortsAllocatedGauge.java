package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.state.ClusterState;

public class UdpPortsAllocatedGauge implements Gauge<Integer> {

    private final ClusterState clusterState;

    public UdpPortsAllocatedGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Integer getValue() {
        var total = 0;
        for (final var entry : clusterState.getNodes().entrySet()) {
            if (entry.getValue().getPortAllocations() == null) {
                continue;
            }
            total += entry.getValue().getPortAllocations().getUdp().size();
        }

        return total;
    }
}
