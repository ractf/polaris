package uk.co.ractf.polaris.api.deployment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StaticReplication.class, name = "static")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Replication {

    private final String type;

    protected Replication(@JsonProperty("type") final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
