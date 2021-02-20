package uk.co.ractf.polaris.api.instanceallocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceRequest {

    private final String challengeID;
    private final String userID;
    private final String teamID;

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
