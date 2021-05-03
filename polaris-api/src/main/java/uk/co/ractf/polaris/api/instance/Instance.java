package uk.co.ractf.polaris.api.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.random.RandomEnv;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.task.TaskId;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an instance of a {@link Challenge} currently scheduled on Polaris
 *
 * <pre>
 *     {
 *         "id": "39b8db8f-c071-4aeb-aee3-147c4219688b",
 *         "deployment": "exampleDeployment1",
 *         "challenge": "hello-world",
 *         "host": "embedded"
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance extends JsonRepresentable {

    private final String id;
    private final TaskId taskId;
    private final String hostId;
    private final List<InstancePortBinding> portBindings;
    private final Map<String, String> randomEnv;


    /**
     * @param id id of the instance
     * @param taskId the task id
     * @param hostId the node id
     * @param portBindings which ports should be bound
     * @param randomEnv the random env vars
     */
    @Contract(pure = true)
    public Instance(@JsonProperty("id") final String id,
                    @JsonProperty("taskId") final TaskId taskId,
                    @JsonProperty("host") final String hostId,
                    @JsonProperty("ports") final List<InstancePortBinding> portBindings,
                    @JsonProperty("randomEnv") final Map<String, String> randomEnv) {
        this.id = id;
        this.taskId = taskId;
        this.hostId = hostId;
        this.portBindings = portBindings;
        this.randomEnv = randomEnv;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("taskId")
    public TaskId getTaskId() {
        return taskId;
    }

    @JsonProperty("host")
    public String getNodeId() {
        return hostId;
    }

    /**
     * @return the ports the instance is using
     */
    @JsonProperty("ports")
    public List<InstancePortBinding> getPortBindings() {
        return portBindings;
    }

    /**
     * @return the {@link RandomEnv} variables that have been generated for the pod
     */
    @JsonProperty("randomEnv")
    public Map<String, String> getRandomEnv() {
        return randomEnv;
    }

    /**
     * @param portBinding the port binding to add
     */
    public void addPortBinding(final InstancePortBinding portBinding) {
        portBindings.add(portBinding);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Instance)) return false;
        final Instance instance = (Instance) o;
        return Objects.equals(id, instance.id) && Objects.equals(taskId, instance.taskId) &&
                Objects.equals(hostId, instance.hostId) && Objects.equals(portBindings, instance.portBindings) &&
                Objects.equals(randomEnv, instance.randomEnv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskId, hostId, portBindings, randomEnv);
    }
}
