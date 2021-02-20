package uk.co.ractf.polaris.api.pod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.healthcheck.HealthCheck;
import uk.co.ractf.polaris.api.random.RandomEnv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Container extends Pod {

    private final String image;
    private final String repo;
    private final List<String> entrypoint;
    private final Map<String, String> env;
    private final Map<String, RandomEnv> randomEnv;
    private final Map<String, String> labels;
    private final List<String> affinity;
    private final List<String> antiAffinity;
    private final ResourceQuota resourceQuota;
    private final String restartPolicy;
    private final List<String> capDrop;
    private final List<String> capAdd;
    private final List<HealthCheck> healthChecks;
    private final Integer terminationTimeout;
    private final List<PortMapping> portMappings;
    private final Map<String, String> metadata;

    private final Map<String, String> generatedRandomEnv = new HashMap<>();

    public Container(
            @JsonProperty("type") final String type,
            @JsonProperty("id") final String id,
            @JsonProperty("image") final String image,
            @JsonProperty("repo") final String repo,
            @JsonProperty("entrypoint") final List<String> entrypoint,
            @JsonProperty("env") final Map<String, String> env,
            @JsonProperty("randomEnv") final Map<String, RandomEnv> randomEnv,
            @JsonProperty("labels") final Map<String, String> labels,
            @JsonProperty("affinity") final List<String> affinity,
            @JsonProperty("antiaffinity") final List<String> antiAffinity,
            @JsonProperty("resourceQuota") final ResourceQuota resourceQuota,
            @JsonProperty("restartPolicy") final String restartPolicy,
            @JsonProperty("capDrop") final List<String> capDrop,
            @JsonProperty("capAdd") final List<String> capAdd,
            @JsonProperty("healthcheck") final List<HealthCheck> healthChecks,
            @JsonProperty("terminationTimeout") final Integer terminationTimeout,
            @JsonProperty("ports") final List<PortMapping> portMappings,
            @JsonProperty("metadata") final Map<String, String> metadata) {
        super(type, id);
        this.image = image;
        this.repo = repo;
        this.entrypoint = entrypoint;
        this.env = env;
        this.randomEnv = randomEnv;
        this.labels = labels;
        this.affinity = affinity;
        this.antiAffinity = antiAffinity;
        this.resourceQuota = resourceQuota;
        this.restartPolicy = restartPolicy;
        this.capDrop = capDrop;
        this.capAdd = capAdd;
        this.healthChecks = healthChecks;
        this.terminationTimeout = terminationTimeout;
        this.portMappings = portMappings;
        this.metadata = metadata;
    }

    public String getImage() {
        return image;
    }

    public String getRepo() {
        return repo;
    }

    public List<String> getEntrypoint() {
        return entrypoint;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public Map<String, RandomEnv> getRandomEnv() {
        return randomEnv;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public List<String> getAffinity() {
        return affinity;
    }

    public List<String> getAntiAffinity() {
        return antiAffinity;
    }

    public ResourceQuota getResourceQuota() {
        return resourceQuota;
    }

    public String getRestartPolicy() {
        return restartPolicy;
    }

    public List<String> getCapDrop() {
        return capDrop;
    }

    public List<String> getCapAdd() {
        return capAdd;
    }

    public List<HealthCheck> getHealthChecks() {
        return healthChecks;
    }

    public Integer getTerminationTimeout() {
        return terminationTimeout;
    }

    public List<PortMapping> getPortMappings() {
        return portMappings;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @JsonIgnoreProperties
    public List<String> getFullEnv() {
        final List<String> list = new ArrayList<>();
        for (final Map.Entry<String, String> entry : env.entrySet()) {
            list.add(entry.getKey() + "=" + entry.getValue());
        }
        for (final Map.Entry<String, RandomEnv> entry : randomEnv.entrySet()) {
            final String generated = entry.getValue().generate();
            generatedRandomEnv.put(entry.getKey(), generated);
            list.add(entry.getKey() + "=" + generated);
        }
        return list;
    }

    @JsonIgnoreProperties
    public Map<String, String> getGeneratedRandomEnv() {
        return generatedRandomEnv;
    }

}
