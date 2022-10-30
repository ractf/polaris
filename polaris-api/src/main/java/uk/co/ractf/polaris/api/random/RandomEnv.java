package uk.co.ractf.polaris.api.random;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

/**
 * Base class for a randomly generated environment variables
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RandomEnvString.class, name = "string"),
        @JsonSubTypes.Type(value = RandomEnvInteger.class, name = "int")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class RandomEnv extends JsonRepresentable {

    private final String type;
    private final String display;

    /**
     * @param type    the type of random env
     * @param display
     */
    @Contract(pure = true)
    public RandomEnv(@JsonProperty("type") final String type,
                     @JsonProperty("display") final String display) {
        this.type = type;
        this.display = display;
    }

    public String getType() {
        return type;
    }

    public String getDisplay() {
        return display;
    }

    /**
     * @return the randomly generated value
     */
    public abstract String generate();

}
