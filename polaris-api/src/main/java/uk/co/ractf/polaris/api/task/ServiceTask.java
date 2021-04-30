package uk.co.ractf.polaris.api.task;

public abstract class ServiceTask extends Task {

    public ServiceTask(final TaskId id, final Integer version) {
        super(id, version, TaskType.SERVICE);
    }
}
