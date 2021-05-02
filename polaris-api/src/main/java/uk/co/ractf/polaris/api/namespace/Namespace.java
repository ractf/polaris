package uk.co.ractf.polaris.api.namespace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Namespace extends JsonRepresentable {

    private final String name;
    private final Long maxCpu;
    private final Long maxMemory;

    public Namespace(
            @JsonProperty("name") final String name,
            @JsonProperty("maxCpu") final Long maxCpu,
            @JsonProperty("maxMemory") final Long maxMemory) {
        this.name = name;
        this.maxCpu = maxCpu;
        this.maxMemory = maxMemory;
    }

    public String getName() {
        return name;
    }

    public Long getMaxCpu() {
        return maxCpu;
    }

    public Long getMaxMemory() {
        return maxMemory;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Namespace)) return false;
        final Namespace namespace = (Namespace) o;
        return Objects.equals(name, namespace.name) && Objects.equals(maxCpu, namespace.maxCpu) && Objects.equals(maxMemory, namespace.maxMemory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, maxCpu, maxMemory);
    }
}
