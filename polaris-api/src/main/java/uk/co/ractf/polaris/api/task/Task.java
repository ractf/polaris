package uk.co.ractf.polaris.api.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Task extends JsonRepresentable {

    private final String id;
    private final TaskType taskType;

    public Task(final String id, final TaskType taskType) {
        this.id = id;
        this.taskType = taskType;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public TaskType getTaskType() {
        return taskType;
    }
}
