package uk.co.ractf.polaris.controller.scheduler;

import com.google.inject.ImplementedBy;
import uk.co.ractf.polaris.api.task.Task;

@ImplementedBy(DefaultScheduler.class)
public interface Scheduler {

    void schedule(final Task task);

    void deschedule(final Task task);

}
