package uk.co.ractf.polaris.api.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.random.RandomEnv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class Instance {

    private final String id;
    private final String deploymentID;
    private final String challengeID;
    private final String hostID;
    private final List<InstancePortBinding> portBindings = new ArrayList<>();
    private final Map<String, String> randomEnv = new HashMap<>();

    /**
     * The
     *
     * @param id the id of the instance
     * @param deploymentID the id of the deployment it belongs to
     * @param challengeID the id of the challenge it belongs to
     * @param hostID the id of the host its scheduled on
     */
    @Contract(pure = true)
    public Instance(
            @JsonProperty("id") final String id,
            @JsonProperty("deployment") final String deploymentID,
            @JsonProperty("challenge") final String challengeID,
            @JsonProperty("host") final String hostID) {
        this.id = id;
        this.deploymentID = deploymentID;
        this.challengeID = challengeID;
        this.hostID = hostID;
    }

    @JsonProperty("id")
    public String getID() {
        return id;
    }

    @JsonProperty("deployment")
    public String getDeploymentID() {
        return deploymentID;
    }

    @JsonProperty("challenge")
    public String getChallengeID() {
        return challengeID;
    }

    @JsonProperty("host")
    public String getHostID() {
        return hostID;
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

}
