package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.api.pod.ResourceLimited;
import uk.co.ractf.polaris.api.pod.ResourceQuota;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.HashMap;
import java.util.Map;

public class MemoryAllocatedGauge implements Gauge<Long> {

    private final ClusterState clusterState;

    public MemoryAllocatedGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Long getValue() {
        long totalAllocated = 0;
        final Map<String, Instance> instanceMap = clusterState.getInstances();
        final Map<String, Integer> instanceCounts = new HashMap<>();
        for (final Map.Entry<String, Instance> entry : instanceMap.entrySet()) {
            instanceCounts.put(entry.getValue().getChallengeId(), instanceCounts.getOrDefault(entry.getValue().getChallengeId(), 0) + 1);
        }

        for (final Map.Entry<String, Challenge> challengeEntry : clusterState.getChallenges().entrySet()) {
            long total = 0;
            for (final Pod pod : challengeEntry.getValue().getPods()) {
                if (pod instanceof ResourceLimited) {
                    final ResourceQuota resourceQuota = ((ResourceLimited) pod).getResourceQuota();
                    if (resourceQuota == null) {
                        continue;
                    }
                    final long mem = resourceQuota.getMemory() == null ? 0 : resourceQuota.getMemory();
                    total += mem;
                }
            }

            totalAllocated += total * instanceCounts.getOrDefault(challengeEntry.getKey(), 0);
        }

        return totalAllocated;
    }
}
