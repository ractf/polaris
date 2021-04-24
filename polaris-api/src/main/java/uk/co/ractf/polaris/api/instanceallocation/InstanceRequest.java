package uk.co.ractf.polaris.api.instanceallocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.instance.Instance;

import java.util.Objects;

/**
 * The details of a user's {@link Instance} allocation request
 *
 * <pre>
 *     {
 *         "challenge": "hello-world",
 *         "user": "12",
 *         "team": "44"
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceRequest extends JsonRepresentable {

    private final String challengeID;
    private final String userID;
    private final String teamID;

    /**
     * @param challengeID the challenge id
     * @param userID      the user id
     * @param teamID      the team id
     */
    @Contract(pure = true)
    public InstanceRequest(
            @JsonProperty("challenge") final String challengeID,
            @JsonProperty("user") final String userID,
            @JsonProperty("team") final String teamID) {
        this.challengeID = challengeID;
        this.userID = userID;
        this.teamID = teamID;
    }

    public String getChallengeID() {
        return challengeID;
    }

    public String getUserID() {
        return userID;
    }

    public String getTeamID() {
        return teamID;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final InstanceRequest that = (InstanceRequest) o;
        return Objects.equals(challengeID, that.challengeID) && Objects.equals(userID, that.userID) && Objects.equals(teamID, that.teamID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(challengeID, userID, teamID);
    }
}
