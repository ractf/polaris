package uk.co.ractf.polaris.api.namespace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Namespace extends JsonRepresentable {

    private final String name;
    private final Boolean hasResourceLimit;
    private final Long maxCpu;
    private final Long maxMemory;
    private final Long allocatedCpu;
    private final Long allocatedMemory;
    private final List<String> allowedCapAdd;
    private final List<String> allowedCapDrop;
    private final String apiUrl;
    private final String apiToken;

    public Namespace(
            @JsonProperty("name") final String name,
            @JsonProperty("hasResourceLimit") final Boolean hasResourceLimit,
            @JsonProperty("maxCpu") final Long maxCpu,
            @JsonProperty("maxMemory") final Long maxMemory,
            @JsonProperty("allocatedCpu") final Long allocatedCpu,
            @JsonProperty("allocatedMemory") final Long allocatedMemory,
            @JsonProperty("allowedCapAdd") final List<String> allowedCapAdd,
            @JsonProperty("allowedCapDrop") final List<String> allowedCapDrop,
            @JsonProperty("apiUrl") final String apiUrl,
            @JsonProperty("apiToken") final String apiToken) {
        this.name = name;
        this.hasResourceLimit = hasResourceLimit;
        this.maxCpu = maxCpu;
        this.maxMemory = maxMemory;
        this.allocatedCpu = allocatedCpu;
        this.allocatedMemory = allocatedMemory;
        this.allowedCapAdd = allowedCapAdd;
        this.allowedCapDrop = allowedCapDrop;
        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
    }

    public String getName() {
        return name;
    }

    public Boolean getHasResourceLimit() {
        return hasResourceLimit;
    }

    public Long getMaxCpu() {
        return maxCpu;
    }

    public Long getMaxMemory() {
        return maxMemory;
    }

    public Long getAllocatedCpu() {
        return allocatedCpu;
    }

    public Long getAllocatedMemory() {
        return allocatedMemory;
    }

    public List<String> getAllowedCapAdd() {
        return allowedCapAdd;
    }

    public List<String> getAllowedCapDrop() {
        return allowedCapDrop;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiToken() {
        return apiToken;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Namespace)) return false;
        final Namespace namespace = (Namespace) o;
        return Objects.equals(name, namespace.name) && Objects.equals(hasResourceLimit, namespace.hasResourceLimit) &&
                Objects.equals(maxCpu, namespace.maxCpu) && Objects.equals(maxMemory, namespace.maxMemory) &&
                Objects.equals(allocatedCpu, namespace.allocatedCpu) &&
                Objects.equals(allocatedMemory, namespace.allocatedMemory) &&
                Objects.equals(allowedCapAdd, namespace.allowedCapAdd) &&
                Objects.equals(allowedCapDrop, namespace.allowedCapDrop) && Objects.equals(apiUrl, namespace.apiUrl) &&
                Objects.equals(apiToken, namespace.apiToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, hasResourceLimit, maxCpu, maxMemory, allocatedCpu, allocatedMemory, allowedCapAdd,
                allowedCapDrop, apiUrl, apiToken);
    }
}
