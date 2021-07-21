package uk.co.ractf.polaris.api.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dockerjava.api.model.AuthConfig;
import uk.co.ractf.polaris.api.namespace.NamespacedId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardRegistryCredentials extends ContainerRegistryCredentials {

    private final AuthConfig authConfig;

    public StandardRegistryCredentials(
            @JsonProperty("id") final NamespacedId id,
            @JsonProperty("type") final String type,
            @JsonProperty("authConfig") final AuthConfig authConfig) {
        super(id, type);
        this.authConfig = authConfig;
    }

    public AuthConfig getAuthConfig() {
        return authConfig;
    }
}
