package uk.co.ractf.polaris.api.pod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.Objects;

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
public class ResourceQuota extends JsonRepresentable {

    private final Long memory;
    private final Long swap;
    private final Long nanocpu;

    /**
     * @param memory   the memory limit (bytes)
     * @param swap     the swap limit (bytes)
     * @param nanocpu how many nano cpus to assign
     */
    @Contract(pure = true)
    public ResourceQuota(
            @JsonProperty("memory") final Long memory,
            @JsonProperty("swap") final Long swap,
            @JsonProperty("nanocpu") final Long nanocpu) {
        this.memory = memory;
        this.swap = swap;
        this.nanocpu = nanocpu;
    }

    public Long getMemory() {
        return memory;
    }

    public Long getSwap() {
        return swap;
    }

    public Long getNanocpu() {
        return nanocpu;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ResourceQuota that = (ResourceQuota) o;
        return Objects.equals(memory, that.memory) && Objects.equals(swap, that.swap) && Objects.equals(nanocpu, that.nanocpu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memory, swap, nanocpu);
    }
}
