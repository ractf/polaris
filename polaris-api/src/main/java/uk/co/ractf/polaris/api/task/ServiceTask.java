package uk.co.ractf.polaris.api.task;

public abstract class ServiceTask extends Task {

    public ServiceTask(final String id) {
        super(id, TaskType.SERVICE);
    }
}
