package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.api.pod.ResourceLimited;
import uk.co.ractf.polaris.api.pod.ResourceQuota;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Map;

public class MemoryAllocatedGauge implements Gauge<Long> {

    private final ClusterState clusterState;

    public MemoryAllocatedGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Long getValue() {
        long totalAllocated = 0;
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

            for (final Deployment deployment : clusterState.getDeploymentsOfChallenge(challengeEntry.getKey())) {
                totalAllocated += total * clusterState.getInstancesForDeployment(deployment.getId()).size();
            }
        }

        return totalAllocated;
    }
}
