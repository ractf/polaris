package uk.co.ractf.polaris.api.deployment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.Objects;

/**
 * Represents a deployment of a {@link Challenge}
 *
 * <pre>
 * {
 * 	"id": "example",
 * 	"challenge": "example",
 * 	"replication": {
 * 		"type": "static",
 * 		"amount": 20
 *        },
 * 	"allocation": {
 * 		"sticky": "user",
 * 		"userLimit": 3,
 * 		"teamLimit": 3
 *    }
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Deployment extends JsonRepresentable {

    private final String id;
    private final String challenge;
    private final Replication replication;
    private final Allocation allocation;

    /**
     * Create a deployment
     *
     * @param id          the id of the deployment
     * @param challenge   the challenge id to be deployed
     * @param replication the replication rules
     * @param allocation  the allocation rules
     */
    @Contract(pure = true)
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Deployment that = (Deployment) o;
        return Objects.equals(id, that.id) && Objects.equals(challenge, that.challenge) && Objects.equals(replication, that.replication) && Objects.equals(allocation, that.allocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, challenge, replication, allocation);
    }
}
