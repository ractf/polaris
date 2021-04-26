package uk.co.ractf.polaris.api.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.random.RandomEnv;

import java.util.*;

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
    private final String deploymentId;
    private final String challengeId;
    private final String hostId;
    private final List<InstancePortBinding> portBindings;
    private final Map<String, String> randomEnv;

    /**
     * The
     *
     * @param id           the id of the instance
     * @param deploymentId the id of the deployment it belongs to
     * @param challengeId  the id of the challenge it belongs to
     * @param hostId       the id of the host its scheduled on
     */
    @Contract(pure = true)
    public Instance(@JsonProperty("id") final String id,
                    @JsonProperty("deployment") final String deploymentId,
                    @JsonProperty("challenge") final String challengeId,
                    @JsonProperty("host") final String hostId,
                    @JsonProperty("ports") final List<InstancePortBinding> portBindings,
                    @JsonProperty("randomEnv") final Map<String, String> randomEnv) {
        this.id = id;
        this.deploymentId = deploymentId;
        this.challengeId = challengeId;
        this.hostId = hostId;
        this.portBindings = portBindings;
        this.randomEnv = randomEnv;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("deployment")
    public String getDeploymentId() {
        return deploymentId;
    }

    @JsonProperty("challenge")
    public String getChallengeId() {
        return challengeId;
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
        if (o == null || getClass() != o.getClass()) return false;
        final Instance instance = (Instance) o;
        return Objects.equals(id, instance.id) && Objects.equals(deploymentId, instance.deploymentId) && Objects.equals(challengeId, instance.challengeId) && Objects.equals(hostId, instance.hostId) && Objects.equals(portBindings, instance.portBindings) && Objects.equals(randomEnv, instance.randomEnv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deploymentId, challengeId, hostId, portBindings, randomEnv);
    }
}
