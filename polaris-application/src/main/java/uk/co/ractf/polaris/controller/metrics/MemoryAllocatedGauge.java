package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.api.pod.ResourceLimited;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.HashMap;

public class MemoryAllocatedGauge implements Gauge<Long> {

    private final ClusterState clusterState;

    public MemoryAllocatedGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Long getValue() {
        long totalAllocated = 0;
        final var instanceMap = clusterState.getInstances();
        final var instanceCounts = new HashMap<NamespacedId, Integer>();
        for (final var entry : instanceMap.entrySet()) {
            instanceCounts.put(entry.getValue().getTaskId(), instanceCounts.getOrDefault(entry.getValue().getTaskId(), 0) + 1);
        }

        for (final var challengeEntry : clusterState.getTasks().entrySet()) {
            long total = 0;
            for (final var pod : challengeEntry.getValue().getPods()) {
                if (pod instanceof ResourceLimited) {
                    final var resourceQuota = ((ResourceLimited) pod).getResourceQuota();
                    if (resourceQuota == null) {
                        continue;
                    }
                    final var mem = resourceQuota.getMemory() == null ? 0 : resourceQuota.getMemory();
                    total += mem;
                }
            }

            totalAllocated += total * instanceCounts.getOrDefault(challengeEntry.getKey(), 0);
        }

        return totalAllocated;
    }
}
