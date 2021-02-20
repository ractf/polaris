package uk.co.ractf.polaris.api.random;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.ThreadLocalRandom;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomEnvInteger extends RandomEnv {

    private final Integer min;
    private final Integer max;

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

}
