package uk.co.ractf.polaris.api.instanceallocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.task.TaskId;

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

    private final TaskId challenge;
    private final String user;
    private final String team;

    /**
     * @param challenge the challenge id
     * @param user      the user id
     * @param team      the team id
     */
    @Contract(pure = true)
    public InstanceRequest(
            @JsonProperty("challenge") final TaskId challenge,
            @JsonProperty("user") final String user,
            @JsonProperty("team") final String team) {
        this.challenge = challenge;
        this.user = user;
        this.team = team;
    }

    public TaskId getTaskId() {
        return challenge;
    }

    public String getUser() {
        return user;
    }

    public String getTeam() {
        return team;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final InstanceRequest that = (InstanceRequest) o;
        return Objects.equals(challenge, that.challenge) && Objects.equals(user, that.user) && Objects.equals(team, that.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(challenge, user, team);
    }
}
