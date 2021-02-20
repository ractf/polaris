package uk.co.ractf.polaris.api.instanceallocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.instance.Instance;

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
public class InstanceRequest {

    private final String challengeID;
    private final String userID;
    private final String teamID;

    /**
     * @param challengeID the challenge id
     * @param userID the user id
     * @param teamID the team id
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

}
