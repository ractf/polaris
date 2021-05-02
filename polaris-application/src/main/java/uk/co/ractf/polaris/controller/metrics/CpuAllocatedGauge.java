package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.api.pod.ResourceLimited;
import uk.co.ractf.polaris.api.pod.ResourceQuota;
import uk.co.ractf.polaris.api.task.TaskId;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.HashMap;
import java.util.Map;

public class CpuAllocatedGauge implements Gauge<Double> {

    private final ClusterState clusterState;

    public CpuAllocatedGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Double getValue() {
        double totalAllocated = 0;
        final var instanceCounts = clusterState.getInstanceCounts();

        for (final var challengeEntry : clusterState.getTasks().entrySet()) {
            double total = 0;
            for (final var pod : challengeEntry.getValue().getPods()) {
                if (pod instanceof ResourceLimited) {
                    final var resourceQuota = ((ResourceLimited) pod).getResourceQuota();
                    if (resourceQuota == null) {
                        continue;
                    }
                    final long cpu = resourceQuota.getNanocpu() == null ? 0 : resourceQuota.getNanocpu();
                    total += (cpu / 1_000_000_000D);
                }
            }

            totalAllocated += total * instanceCounts.getOrDefault(challengeEntry.getKey(), 0);
        }

        return totalAllocated;
    }
}
