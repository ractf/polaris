package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.inject.Inject;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.HashMap;
import java.util.Map;

public class PolarisMetricSet implements MetricSet {

    private final ClusterState clusterState;

    @Inject
    public PolarisMetricSet(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> metrics = new HashMap<>();
        metrics.put("challenges.count", new ChallengesGauge(clusterState));
        metrics.put("deployments.count", new DeploymentsGauge(clusterState));
        metrics.put("hosts.count", new HostsGauge(clusterState));
        metrics.put("instances.count", new InstancesGauge(clusterState));
        metrics.put("resources.memory.allocated", new MemoryAllocatedGauge(clusterState));
        metrics.put("resources.cpu.allocated", new CpuAllocatedGauge(clusterState));
        return metrics;
    }
}
