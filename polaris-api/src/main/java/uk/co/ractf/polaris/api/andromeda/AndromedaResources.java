package uk.co.ractf.polaris.api.andromeda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

/**
 * Represents the Andromeda resource allocation format.
 * {
 *     "memory": 631242752,
 *     "cpus": "0.2"
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class AndromedaResources extends JsonRepresentable {

    private final Integer memory;
    private final String cpus;

    public AndromedaResources(
            @JsonProperty("memory") final Integer memory,
            @JsonProperty("cpus") final String cpus) {
        this.memory = memory;
        this.cpus = cpus;
    }

    public Integer getMemory() {
        return memory;
    }

    public String getCpus() {
        return cpus;
    }
}
