package uk.co.ractf.polaris.api.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.deployment.Replication;
import uk.co.ractf.polaris.api.pod.Pod;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Challenge.class, name = "challenge"),
})
public abstract class Task extends JsonRepresentable {

    private final TaskId id;
    private final Integer version;
    private final TaskType taskType;
    private final Replication replication;
    private final List<Pod> pods;

    public Task(final TaskId id, final Integer version, final TaskType taskType, final Replication replication, final List<Pod> pods) {
        this.id = id;
        this.version = version;
        this.taskType = taskType;
        this.replication = replication;
        this.pods = pods;
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

    public Replication getReplication() {
        return replication;
    }

    public List<Pod> getPods() {
        return pods;
    }

    /**
     * Gets a {@link Pod} from this task that has a given id
     *
     * @param id the id of the pod
     * @return the pod
     */
    @JsonIgnoreProperties
    public Pod getPod(final String id) {
        for (final var pod : getPods()) {
            if (pod.getId().equals(id)) {
                return pod;
            }
        }

        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        final Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(version, task.version) && taskType == task.taskType && Objects.equals(replication, task.replication) && Objects.equals(pods, task.pods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, taskType, replication, pods);
    }
}
