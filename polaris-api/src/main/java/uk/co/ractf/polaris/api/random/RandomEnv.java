package uk.co.ractf.polaris.api.random;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RandomEnvString.class, name = "string"),
        @JsonSubTypes.Type(value = RandomEnvInteger.class, name = "int")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class RandomEnv {

    private final String type;

    public RandomEnv(@JsonProperty("type") final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public abstract String generate();

}
