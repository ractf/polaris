package uk.co.ractf.polaris.api.deployment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.Objects;

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
public class Allocation extends JsonRepresentable {

    private final String sticky;
    private final Integer userLimit;
    private final Integer teamLimit;
    private final boolean singleUser;

    /**
     * Create an allocation
     *
     * @param sticky     should the allocator make instances sticky on "user" or "team"
     * @param userLimit  max amount of users on an instance
     * @param teamLimit  max amount of teams on an instance
     * @param singleUser
     */
    @Contract(pure = true)
    public Allocation(
            @JsonProperty("sticky") final String sticky,
            @JsonProperty("userLimit") final Integer userLimit,
            @JsonProperty("teamLimit") final Integer teamLimit,
            @JsonProperty("strict") final boolean singleUser) {
        this.sticky = sticky;
        this.userLimit = userLimit;
        this.teamLimit = teamLimit;
        this.singleUser = singleUser;
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

    public boolean isSingleUser() {
        return singleUser;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Allocation)) return false;
        final Allocation that = (Allocation) o;
        return singleUser == that.singleUser && Objects.equals(sticky, that.sticky) && Objects.equals(userLimit, that.userLimit) && Objects.equals(teamLimit, that.teamLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sticky, userLimit, teamLimit, singleUser);
    }
}
