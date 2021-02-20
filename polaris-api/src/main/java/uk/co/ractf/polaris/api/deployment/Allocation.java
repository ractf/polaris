package uk.co.ractf.polaris.api.deployment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Allocation {

    private final String sticky;
    private final Integer userLimit;
    private final Integer teamLimit;

    public Allocation(
            @JsonProperty("sticky") final String sticky,
            @JsonProperty("userLimit") final Integer userLimit,
            @JsonProperty("teamLimit") final Integer teamLimit) {
        this.sticky = sticky;
        this.userLimit = userLimit;
        this.teamLimit = teamLimit;
    }

    public String getSticky() {
        return sticky;
    }

    public Integer getUserLimit() {
        return userLimit;
    }

    public Integer getTeamLimit() {
        return teamLimit;
    }
}
