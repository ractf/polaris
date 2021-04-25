package uk.co.ractf.polaris.api.andromeda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a challenge that can be submitted via the Andromeda emulation resource. This will be converted to a
 * {@link uk.co.ractf.polaris.api.challenge.Challenge} and a {@link uk.co.ractf.polaris.api.deployment.Deployment}
 * {
 * "name": "fffff",
 * "port": 80,
 * "replicas": 10,
 * "resources": {
 * "memory": 631242752,
 * "cpus": "0.2"
 * },
 * "image": "docker.pkg.github.com/ractf/challenges/example:latest"
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class AndromedaChallenge {

    private final String name;
    private final Integer port;
    private final Integer replicas;
    private final AndromedaResources resources;
    private final String image;
    private final AndromedaAuthentication registryAuth;

    public AndromedaChallenge(
            @JsonProperty("name") final String name,
            @JsonProperty("port") final Integer port,
            @JsonProperty("replicas") final Integer replicas,
            @JsonProperty("resources") final AndromedaResources resources,
            @JsonProperty("image") final String image,
            @JsonProperty("registryAuth") final AndromedaAuthentication registryAuth) {
        this.name = name;
        this.port = port;
        this.replicas = replicas;
        this.resources = resources;
        this.image = image;
        this.registryAuth = registryAuth;
    }

    public String getName() {
        return name;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public AndromedaResources getResources() {
        return resources;
    }

    public String getImage() {
        return image;
    }

    public AndromedaAuthentication getRegistryAuth() {
        return registryAuth;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof AndromedaChallenge)) return false;
        final AndromedaChallenge that = (AndromedaChallenge) o;
        return Objects.equals(name, that.name) && Objects.equals(port, that.port) && Objects.equals(replicas, that.replicas) && Objects.equals(resources, that.resources) && Objects.equals(image, that.image) && Objects.equals(registryAuth, that.registryAuth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, port, replicas, resources, image, registryAuth);
    }
}
