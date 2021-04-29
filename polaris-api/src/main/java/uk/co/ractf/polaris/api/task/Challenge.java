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

    private final String id;
    private final List<Pod> pods;
    private final Replication replication;
    private final Allocation allocation;

    /**
     * Create a challenge
     *  @param id   the id of the challenge
     * @param pods the pods making up the challenge
     * @param replication the replication detail
     * @param allocation the instance allocation rules
     */
    @Contract(pure = true)
    public Challenge(
            @JsonProperty("id") final String id,
            @JsonProperty("pods") final List<Pod> pods,
            @JsonProperty("replication") final Replication replication,
            @JsonProperty("allocation") final Allocation allocation) {
        super(id);
        this.id = id;
        this.pods = pods;
        this.replication = replication;
        this.allocation = allocation;
    }

    public String getId() {
        return id;
    }

    public List<Pod> getPods() {
        return pods;
    }

    public Replication getReplication() {
        return replication;
    }

    public Allocation getAllocation() {
        return allocation;
    }

    /**
     * Gets a {@link Pod} from this challenge that has a given id
     *
     * @param id the id of the pod
     * @return the pod
     */
    @JsonIgnoreProperties
    public Pod getPod(final String id) {
        for (final Pod pod : pods) {
            if (pod.getId().equals(id)) {
                return pod;
            }
        }

        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Challenge challenge = (Challenge) o;
        return Objects.equals(id, challenge.id) && Objects.equals(pods, challenge.pods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pods);
    }
}
