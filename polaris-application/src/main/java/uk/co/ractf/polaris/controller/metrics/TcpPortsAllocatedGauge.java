package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Map;

public class TcpPortsAllocatedGauge implements Gauge<Integer> {

    private final ClusterState clusterState;

    public TcpPortsAllocatedGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Integer getValue() {
        var total = 0;
        for (final var entry : clusterState.getNodes().entrySet()) {
            if (entry.getValue().getPortAllocations() == null) {
                continue;
            }
            total += entry.getValue().getPortAllocations().getTcp().size();
        }

        return total;
    }
}
