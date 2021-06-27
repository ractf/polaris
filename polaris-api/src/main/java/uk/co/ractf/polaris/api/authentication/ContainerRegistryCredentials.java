package uk.co.ractf.polaris.api.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.namespace.NamespacedId;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AWSCredentials.class, name = "aws"),
        @JsonSubTypes.Type(value = StandardRegistryCredentials.class, name = "standard"),
})
public abstract class ContainerRegistryCredentials extends JsonRepresentable {

    private final NamespacedId id;
    private final String type;

    public ContainerRegistryCredentials(
            @JsonProperty("id") final NamespacedId id,
            @JsonProperty("type") final String type) {
        this.id = id;
        this.type = type;
    }

    public NamespacedId getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
