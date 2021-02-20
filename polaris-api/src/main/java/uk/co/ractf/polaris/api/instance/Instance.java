package uk.co.ractf.polaris.api.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance {

    private final String id;
    private final String deploymentID;
    private final String challengeID;
    private final String hostID;
    private final List<InstancePortBinding> portBindings = new ArrayList<>();
    private final Map<String, String> randomEnv = new HashMap<>();

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

    @JsonProperty("ports")
    public List<InstancePortBinding> getPortBindings() {
        return portBindings;
    }

    @JsonProperty("randomEnv")
    public Map<String, String> getRandomEnv() {
        return randomEnv;
    }

    public void addPortBinding(final InstancePortBinding portBinding) {
        portBindings.add(portBinding);
    }

    public void addRandomEnv(final String key, final String value) {
        randomEnv.put(key, value);
    }

}
