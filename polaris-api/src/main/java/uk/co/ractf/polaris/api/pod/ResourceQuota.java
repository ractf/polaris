package uk.co.ractf.polaris.api.pod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

/**
 * Represents the resource quote for a pod
 *
 * <pre>
 *     {
 *         "memory": 512000000,
 *         "swap": 0,
 *         "nanocpu": 100000000
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceQuota {

    private final Long memory;
    private final Long swap;
    private final Long nanoCPUs;

    /**
     * @param memory the memory limit (bytes)
     * @param swap the swap limit (bytes)
     * @param nanoCPUs how many nano cpus to assign
     */
    @Contract(pure = true)
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
