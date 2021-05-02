package uk.co.ractf.polaris.controller.scheduler.clusterpredicate;

import com.google.inject.Inject;
import uk.co.ractf.polaris.api.pod.ResourceLimited;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.scheduler.ClusterPredicatePlugin;
import uk.co.ractf.polaris.controller.scheduler.ClusterPredicateResult;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Collections;

public class NamespaceResourcesAvailable implements ClusterPredicatePlugin {

    private final ClusterState clusterState;

    @Inject
    public NamespaceResourcesAvailable(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public ClusterPredicateResult canSchedule(final Task task) {
        final var namespace = clusterState.getNamespace(task.getId().getNamespace());
        if (namespace.getHasResourceLimit()) {
            var spareCpu = namespace.getMaxCpu() - namespace.getAllocatedCpu();
            var spareMemory = namespace.getMaxMemory() - namespace.getAllocatedMemory();
            for (final var pod : task.getPods()) {
                if (pod instanceof ResourceLimited) {
                    final var resourceQuota = ((ResourceLimited) pod).getResourceQuota();
                    spareCpu -= resourceQuota.getNanocpu();
                    spareMemory -= resourceQuota.getMemory();
                }
            }
            if (spareCpu < 0 || spareMemory < 0) {
                return new ClusterPredicateResult(false, Collections.singletonList("Namespace " + namespace.getName() + " out of resources"));
            }
        }
        return ClusterPredicateResult.SCHEDULABLE;
    }

    @Override
    public String getName() {
        return "NamespaceResourcesAvailable";
    }
}
