package uk.co.ractf.polaris.api.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.namespace.NamespacedId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AWSCredentials extends ContainerRegistryCredentials {

    private final String accessKey;
    private final String secretKey;

    public AWSCredentials(
            @JsonProperty("id") final NamespacedId id,
            @JsonProperty("type") final String type,
            @JsonProperty("accessKey") final String accessKey,
            @JsonProperty("secretKey") final String secretKey) {
        super(id, type);
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
