package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Map;

public class UdpPortsAllocatedGauge implements Gauge<Integer> {

    private final ClusterState clusterState;

    public UdpPortsAllocatedGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Integer getValue() {
        int total = 0;
        for (final Map.Entry<String, NodeInfo> entry : clusterState.getNodes().entrySet()) {
            total += entry.getValue().getPortAllocations().getUdp().size();
        }

        return total;
    }
}
