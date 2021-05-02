package uk.co.ractf.polaris.controller.scheduler.clusterpredicate;

import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.scheduler.ClusterPredicatePlugin;
import uk.co.ractf.polaris.controller.scheduler.ClusterPredicateResult;

import java.util.Collections;

public class RegistryAuth implements ClusterPredicatePlugin {

    @Override
    public ClusterPredicateResult canSchedule(final Task task) {
        //TODO
        return new ClusterPredicateResult(true, Collections.emptyList());
    }

    @Override
    public String getName() {
        return "RegistryAuth";
    }
}
