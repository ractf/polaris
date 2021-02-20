package uk.co.ractf.polaris.api.deployment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

/**
 * Represents the constraints within which {@link uk.co.ractf.polaris.api.instance.Instance}s should be allocated to users
 *
 * <pre>
 * {
 * 	 "sticky": "user",
 * 	 "userLimit": 3,
 * 	 "teamLimit": 3
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Allocation {

    private final String sticky;
    private final Integer userLimit;
    private final Integer teamLimit;

    /**
     * Create an allocation
     *
     * @param sticky should the allocator make instances sticky on "user" or "team"
     * @param userLimit max amount of users on an instance
     * @param teamLimit max amount of teams on an instance
     */
    @Contract(pure = true)
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
