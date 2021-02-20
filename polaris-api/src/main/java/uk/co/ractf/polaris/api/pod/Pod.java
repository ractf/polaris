package uk.co.ractf.polaris.api.pod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents a pod that can be scheduled on Polaris
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Container.class, name = "container")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Pod {

    private final String type;
    private final String id;

    /**
     * @param type the type of pod
     * @param id the pod id
     */
    public Pod(@JsonProperty("type") final String type, @JsonProperty("id") final String id) {
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getID() {
        return id;
    }

}
