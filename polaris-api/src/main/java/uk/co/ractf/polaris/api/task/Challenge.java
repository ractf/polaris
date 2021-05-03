package uk.co.ractf.polaris.api.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.deployment.Allocation;
import uk.co.ractf.polaris.api.deployment.Replication;
import uk.co.ractf.polaris.api.pod.Pod;

import java.util.List;
import java.util.Objects;

/**
 * Represents a challenge that can run on Polaris, comprised of one or more {@link Pod}s
 *
 * <pre>
 * {
 *   "id": "example",
 *   "pods": []
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Challenge extends ServiceTask {

    private final Allocation allocation;

    /**
     * Create a challenge
     *
     * @param id          the id of the challenge
     * @param version     version of the challenge
     * @param pods        the pods making up the challenge
     * @param replication the replication detail
     * @param allocation  the instance allocation rules
     */
    @Contract(pure = true)
    public Challenge(
            @JsonProperty("id") final TaskId id,
            @JsonProperty("version") final Integer version,
            @JsonProperty("pods") final List<Pod> pods,
            @JsonProperty("replication") final Replication replication,
            @JsonProperty("allocation") final Allocation allocation) {
        super(id, version, replication, pods);
        this.allocation = allocation;
    }

    public Allocation getAllocation() {
        return allocation;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Challenge)) return false;
        if (!super.equals(o)) return false;
        final Challenge challenge = (Challenge) o;
        return Objects.equals(allocation, challenge.allocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), allocation);
    }
}
