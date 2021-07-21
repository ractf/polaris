package uk.co.ractf.polaris.api.pod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.healthcheck.HealthCheck;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.api.random.RandomEnv;

import java.util.*;

/**
 * An implementation of {@link Pod} that represents a Docker container
 * <pre>
 * {
 *      "type": "container",
 *      "id": "example1",
 *      "image": "docker.io/bfirsh/reticulate-splines",
 *      "repo": "dockerhub",
 *      "entrypoint": [],
 *      "env": {
 *          "aaa": "bbb"
 *      },
 *      "randomEnv": {
 *          "test": {
 *              "type": "string",
 *              "length": 6,
 *              "alphabet": "1234567890abcdef"
 *          },
 *          "test2": {
 *              "type": "int",
 *              "min": 69,
 *              "max": 420
 *          }
 *      },
 *      "labels": {
 *          "key": "value"
 *      },
 *      "affinity": ["aslr"],
 *      "antiaffinity": ["ARM"],
 *      "resourceQuota": {
 *          "memory": 536870912,
 *          "swap": 0,
 *          "nanocpu": 10000000
 *      },
 *      "capDrop": ["BLOCK_SUSPEND"],
 *      "capAdd": ["MKNOD"],
 *      "restartPolicy": "always",
 *      "healthcheck": [
 *          {
 *              "id": "tcpconnect",
 *              "type": "tcp",
 *              "port": 6000,
 *              "timeout": 5
 *          },
 *          {
 *              "id": "flagcheck",
 *              "type": "tcppayload",
 *              "payloads": [
 *                  {
 *                      "type": "send",
 *                      "hex": "deadbeef"
 *                  },
 *                  {
 *                      "type": "receiveexact",
 *                      "hex": "deadbeef"
 *                  },
 *                  {
 *                      "type": "receiveregex",
 *                      "hex": ".*"
 *                  }
 *              ]
 *          },
 *          {
 *              "id": "web",
 *              "type": "http",
 *              "path": "/test",
 *              "port": 80,
 *              "vhost": "dave.lc"
 *          }
 *      ],
 *      "terminationTimeout": 5,
 *      "metadata": {
 *          "aaa": "bbb"
 *      },
 *      "portMappings": [
 *          {
 *              "port": 6000,
 *              "protocol": "tcp",
 *              "advertise": true
 *          }
 *      ]
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Container extends Pod implements ResourceLimited, PodWithPorts, PodWithAffinity, AutoUpdatable {

    private final String image;
    private final String repo;
    private final NamespacedId repoCredentials;
    private final List<String> entrypoint;
    private final Map<String, String> env;
    private final Map<String, RandomEnv> randomEnv;
    private final Map<String, String> labels;
    private final Map<String, String> affinity;
    private final Map<String, String> antiaffinity;
    private final ResourceQuota resourceQuota;
    private final String restartPolicy;
    private final List<String> capDrop;
    private final List<String> capAdd;
    private final List<HealthCheck> healthChecks;
    private final Integer terminationTimeout;
    private final List<PortMapping> ports;
    private final Map<String, String> metadata;
    private final boolean autoUpdate;

    private final Map<String, String> generatedRandomEnv = new HashMap<>();

    /**
     * Creates a container
     * @param type               the type of the pod (container)
     * @param id                 the id of the container
     * @param image              the image to use
     * @param repo               the repository id
     * @param repoCredentials    the credentials to use for the repository
     * @param entrypoint         the container entrypoint
     * @param env                the environment variables to set
     * @param randomEnv          the randomised environment variables to set
     * @param labels             labels to add to the container
     * @param affinity           which types of host a pod should be preferentially scheduled on
     * @param antiaffinity       which types of host a pod should avoid being scheduled on
     * @param resourceQuota      the resource quota of the container
     * @param restartPolicy      the restart policy of the container
     * @param capDrop            capabilities to drop
     * @param capAdd             capabilities to add
     * @param healthChecks       healthchecks for the pod
     * @param terminationTimeout the timeout to use when the pod is terminated
     * @param portMappings       ports to expose
     * @param metadata           other metadata
     * @param autoUpdate         if the container should be automatically updated
     */
    public Container(
            @JsonProperty("type") final String type,
            @JsonProperty("id") final String id,
            @JsonProperty("image") final String image,
            @JsonProperty("repo") final String repo,
            @JsonProperty("repoCredentials") final NamespacedId repoCredentials,
            @JsonProperty("entrypoint") final List<String> entrypoint,
            @JsonProperty("env") final Map<String, String> env,
            @JsonProperty("randomEnv") final Map<String, RandomEnv> randomEnv,
            @JsonProperty("labels") final Map<String, String> labels,
            @JsonProperty("affinity") final Map<String, String> affinity,
            @JsonProperty("antiaffinity") final Map<String, String> antiaffinity,
            @JsonProperty("resourceQuota") final ResourceQuota resourceQuota,
            @JsonProperty("restartPolicy") final String restartPolicy,
            @JsonProperty("capDrop") final List<String> capDrop,
            @JsonProperty("capAdd") final List<String> capAdd,
            @JsonProperty("healthChecks") final List<HealthCheck> healthChecks,
            @JsonProperty("terminationTimeout") final Integer terminationTimeout,
            @JsonProperty("ports") final List<PortMapping> portMappings,
            @JsonProperty("metadata") final Map<String, String> metadata,
            @JsonProperty("autoUpdate") final boolean autoUpdate) {
        super(type, id);
        this.image = image;
        this.repo = repo;
        this.repoCredentials = repoCredentials;
        this.entrypoint = entrypoint;
        this.env = env;
        this.randomEnv = randomEnv;
        this.labels = labels;
        this.affinity = affinity;
        this.antiaffinity = antiaffinity;
        this.resourceQuota = resourceQuota;
        this.restartPolicy = restartPolicy;
        this.capDrop = capDrop;
        this.capAdd = capAdd;
        this.healthChecks = healthChecks;
        this.terminationTimeout = terminationTimeout;
        this.ports = portMappings;
        this.metadata = metadata;
        this.autoUpdate = autoUpdate;
    }

    public String getImage() {
        return image;
    }

    public String getRepo() {
        return repo;
    }

    public NamespacedId getRepoCredentials() {
        return repoCredentials;
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

    @Override
    public Map<String, String> getAffinity() {
        return affinity;
    }

    @Override
    public Map<String, String> getAntiaffinity() {
        return antiaffinity;
    }

    @Override
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

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public List<PortMapping> getPorts() {
        return ports;
    }

    @Override
    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    private void generateRandomEnvIfEmpty() {
        if (generatedRandomEnv.isEmpty()) {
            for (final Map.Entry<String, RandomEnv> entry : randomEnv.entrySet()) {
                final String generated = entry.getValue().generate();
                generatedRandomEnv.put(entry.getKey(), generated);
            }
        }
    }

    @JsonIgnore
    public List<String> getFullEnv() {
        final List<String> list = new ArrayList<>();
        for (final Map.Entry<String, String> entry : env.entrySet()) {
            list.add(entry.getKey() + "=" + entry.getValue());
        }
        generateRandomEnvIfEmpty();
        for (final Map.Entry<String, String> entry : generatedRandomEnv.entrySet()) {
            list.add(entry.getKey() + "=" + entry.getValue());
        }
        return list;
    }

    @JsonIgnore
    public Map<String, String> getGeneratedRandomEnv() {
        generateRandomEnvIfEmpty();
        return generatedRandomEnv;
    }

    @Override
    public boolean canUseRunner(final String runner) {
        return "docker".equals(runner);
    }

    public String getTag() {
        var tag = "latest";
        if (image.replaceAll("http(s?):", "").contains(":")) {
            tag = image.split(":")[1];
        }
        return tag;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Container)) return false;
        final Container container = (Container) o;
        return Objects.equals(image, container.image) && Objects.equals(repo, container.repo) &&
                Objects.equals(repoCredentials, container.repoCredentials) &&
                Objects.equals(entrypoint, container.entrypoint) && Objects.equals(env, container.env) &&
                Objects.equals(randomEnv, container.randomEnv) && Objects.equals(labels, container.labels) &&
                Objects.equals(affinity, container.affinity) && Objects.equals(antiaffinity, container.antiaffinity) &&
                Objects.equals(resourceQuota, container.resourceQuota) &&
                Objects.equals(restartPolicy, container.restartPolicy) && Objects.equals(capDrop, container.capDrop) &&
                Objects.equals(capAdd, container.capAdd) && Objects.equals(healthChecks, container.healthChecks) &&
                Objects.equals(terminationTimeout, container.terminationTimeout) &&
                Objects.equals(ports, container.ports) && Objects.equals(metadata, container.metadata) &&
                Objects.equals(autoUpdate, container.autoUpdate) && Objects.equals(getType(), container.getType()) &&
                Objects.equals(getId(), container.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, repo, repoCredentials, entrypoint, env, randomEnv, labels, affinity, antiaffinity,
                resourceQuota, restartPolicy, capDrop, capAdd, healthChecks, terminationTimeout, ports, metadata,
                autoUpdate, getType(), getId());
    }
}
