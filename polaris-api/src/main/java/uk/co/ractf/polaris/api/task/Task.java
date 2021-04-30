package uk.co.ractf.polaris.api.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Task extends JsonRepresentable {

    private final TaskId id;
    private final Integer version;
    private final TaskType taskType;

    public Task(final TaskId id, final Integer version, final TaskType taskType) {
        this.id = id;
        this.version = version;
        this.taskType = taskType;
    }

    public TaskId getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    @JsonIgnore
    public TaskType getTaskType() {
        return taskType;
    }
}
