package uk.co.ractf.polaris.controller.scheduler;

import com.google.inject.ImplementedBy;
import uk.co.ractf.polaris.api.task.Task;

@ImplementedBy(DefaultTaskScaler.class)
public interface TaskScaler {

    void scaleTask(final Task task);

}
