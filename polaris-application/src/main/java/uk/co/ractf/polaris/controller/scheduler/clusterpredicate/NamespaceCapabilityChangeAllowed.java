package uk.co.ractf.polaris.controller.scheduler.clusterpredicate;

import com.google.inject.Inject;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.scheduler.ClusterPredicatePlugin;
import uk.co.ractf.polaris.controller.scheduler.ClusterPredicateResult;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NamespaceCapabilityChangeAllowed implements ClusterPredicatePlugin {

    private final ClusterState clusterState;

    @Inject
    public NamespaceCapabilityChangeAllowed(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @Override
    public ClusterPredicateResult canSchedule(final Task task) {
        final var namespace = clusterState.getNamespace(task.getId().getNamespace());
        final List<String> usedCapAdd = new ArrayList<>();
        final List<String> usedCapDrop = new ArrayList<>();
        for (final var pod : task.getPods()) {
            if (pod instanceof Container) {
                usedCapAdd.addAll(((Container) pod).getCapAdd());
                usedCapDrop.addAll(((Container) pod).getCapDrop());
            }
        }
        for (final var cap : usedCapAdd) {
            if (!namespace.getAllowedCapAdd().contains(cap)) {
                return new ClusterPredicateResult(false, Collections.singletonList("Cap add " + cap + " is not allowed in namespace " + namespace.getName()));
            }
        }
        for (final var cap : usedCapDrop) {
            if (!namespace.getAllowedCapDrop().contains(cap)) {
                return new ClusterPredicateResult(false, Collections.singletonList("Cap drop " + cap + " is not allowed in namespace " + namespace.getName()));
            }
        }
        return ClusterPredicateResult.SCHEDULABLE;
    }

    @Override
    public String getName() {
        return "NamespaceCapabilityChangeAllowed";
    }
}
