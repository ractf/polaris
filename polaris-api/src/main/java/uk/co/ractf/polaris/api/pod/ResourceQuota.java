package uk.co.ractf.polaris.api.pod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceQuota {

    private final Long memory;
    private final Long swap;
    private final Long nanoCPUs;

    public ResourceQuota(
            @JsonProperty("memory") final Long memory,
            @JsonProperty("swap") final Long swap,
            @JsonProperty("nanocpu") final Long nanoCPUs) {
        this.memory = memory;
        this.swap = swap;
        this.nanoCPUs = nanoCPUs;
    }

    public Long getMemory() {
        return memory;
    }

    public Long getSwap() {
        return swap;
    }

    public Long getNanoCPUs() {
        return nanoCPUs;
    }

}
