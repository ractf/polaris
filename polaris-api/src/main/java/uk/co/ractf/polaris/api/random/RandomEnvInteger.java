package uk.co.ractf.polaris.api.random;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a randomly generated integer environment variable
 *
 * <pre>
 *     {
 *         "type": "int",
 *         "min": 60,
 *         "max": 120
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomEnvInteger extends RandomEnv {

    private final Integer min;
    private final Integer max;

    /**
     * @param type the type of random env (int)
     * @param min  the minimum integer
     * @param max  the maximum integer
     */
    @Contract(pure = true)
    public RandomEnvInteger(
            @JsonProperty("type") final String type,
            @JsonProperty("min") final Integer min,
            @JsonProperty("max") final Integer max) {
        super(type);
        this.min = min;
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    @Override
    public String generate() {
        //TODO: Is this randomness secure? Does it matter?
        return String.valueOf(ThreadLocalRandom.current().nextInt(min, max));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RandomEnvInteger that = (RandomEnvInteger) o;
        return Objects.equals(min, that.min) && Objects.equals(max, that.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }
}
