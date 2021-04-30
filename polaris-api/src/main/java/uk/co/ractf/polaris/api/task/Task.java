package uk.co.ractf.polaris.api.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Challenge.class, name = "challenge"),
})
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
