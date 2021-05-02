package uk.co.ractf.polaris.api.task;

import uk.co.ractf.polaris.api.deployment.Replication;
import uk.co.ractf.polaris.api.pod.Pod;

import java.util.List;

public abstract class ServiceTask extends Task {

    public ServiceTask(final TaskId id, final Integer version, final Replication replication, final List<Pod> pods) {
        super(id, version, TaskType.SERVICE, replication, pods);
    }
}
