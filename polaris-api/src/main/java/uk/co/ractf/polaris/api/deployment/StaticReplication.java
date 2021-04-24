package uk.co.ractf.polaris.api.deployment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

/**
 * Represents a {@link Replication} strategy that deploys a consistent number of replicas.
 *
 * <pre>
 *     {
 * 		"type": "static",
 * 		"amount": 20
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaticReplication extends Replication {

    private final Integer amount;

    /**
     * Create a StaticReplication
     *
     * @param type   the type of replication (static)
     * @param amount the amount of replicas
     */
    @Contract(pure = true)
    public StaticReplication(
            @JsonProperty("type") final String type,
            @JsonProperty("amount") final Integer amount) {
        super(type);
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StaticReplication that = (StaticReplication) o;
        return Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
