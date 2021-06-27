package uk.co.ractf.polaris.api.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.namespace.NamespacedId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AWSCredentials extends ContainerRegistryCredentials {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String authorizationToken;

    public AWSCredentials(
            @JsonProperty("id") final NamespacedId id,
            @JsonProperty("type") final String type,
            @JsonProperty("accessKey") final String accessKey,
            @JsonProperty("secretKey") final String secretKey,
            @JsonProperty("region") final String region,
            @JsonProperty("authorizationToken") final String authorizationToken) {
        super(id, type);
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.authorizationToken = authorizationToken;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getRegion() {
        return region;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }
}
