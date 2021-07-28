package uk.co.ractf.polaris.api.registry.credentials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.namespace.NamespacedId;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
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
