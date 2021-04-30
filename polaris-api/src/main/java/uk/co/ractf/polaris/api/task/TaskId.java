package uk.co.ractf.polaris.api.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public class TaskId {

    private final String namespace;
    private final String id;

    public TaskId(final String namespace, final String id) {
        this.namespace = namespace;
        this.id = id;
    }

    @JsonCreator
    public TaskId(final String taskId) {
        final String[] parts = taskId.split(":");
        if (parts.length > 2) {
            throw new IllegalArgumentException("Invalid task id: " + taskId);
        } else if (parts.length == 2) {
            namespace = parts[0];
            id = parts[1];
        } else {
            namespace = "polaris";
            id = parts[0];
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public String getId() {
        return id;
    }

    @Override
    @JsonValue
    public String toString() {
        return namespace + ":" + id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskId)) return false;
        final TaskId taskId = (TaskId) o;
        return Objects.equals(namespace, taskId.namespace) && Objects.equals(id, taskId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, id);
    }
}
