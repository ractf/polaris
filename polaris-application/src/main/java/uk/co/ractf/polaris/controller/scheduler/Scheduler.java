package uk.co.ractf.polaris.controller.scheduler;

import uk.co.ractf.polaris.api.task.Task;

public interface Scheduler {

    void schedule(final Task task);

    void deschedule(final Task task);

}
