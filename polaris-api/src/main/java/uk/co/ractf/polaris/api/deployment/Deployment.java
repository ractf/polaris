package uk.co.ractf.polaris.api.deployment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Deployment {

    private final String id;
    private final String challenge;
    private final Replication replication;
    private final Allocation allocation;

    public Deployment(
            @JsonProperty("id") final String id,
            @JsonProperty("challenge") final String challenge,
            @JsonProperty("replication") final Replication replication,
            @JsonProperty("allocation") final Allocation allocation) {
        this.id = id;
        this.challenge = challenge;
        this.replication = replication;
        this.allocation = allocation;
    }

    public String getId() {
        return id;
    }

    public String getChallenge() {
        return challenge;
    }

    public Replication getReplication() {
        return replication;
    }

    public Allocation getAllocation() {
        return allocation;
    }
}
