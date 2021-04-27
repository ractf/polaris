package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.api.pod.ResourceLimited;
import uk.co.ractf.polaris.api.pod.ResourceQuota;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Map;

public class CpuAllocatedGauge implements Gauge<Double> {

    private final ClusterState clusterState;

    public CpuAllocatedGauge(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public Double getValue() {
        double totalAllocated = 0;
        for (final Map.Entry<String, Challenge> challengeEntry : clusterState.getChallenges().entrySet()) {
            double total = 0;
            for (final Pod pod : challengeEntry.getValue().getPods()) {
                if (pod instanceof ResourceLimited) {
                    final ResourceQuota resourceQuota = ((ResourceLimited) pod).getResourceQuota();
                    if (resourceQuota == null) {
                        continue;
                    }
                    final long cpu = resourceQuota.getNanocpu() == null ? 0 : resourceQuota.getNanocpu();
                    total += (cpu / 1_000_000_000D);
                }
            }

            for (final Deployment deployment : clusterState.getDeploymentsOfChallenge(challengeEntry.getKey())) {
                totalAllocated += total * clusterState.getInstancesForDeployment(deployment.getId()).size();
            }
        }

        return totalAllocated;
    }
}
