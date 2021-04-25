package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.pod.Pod;

/**
 * Base class for all healthchecks that can be ran against a {@link Pod}
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TcpHealthCheck.class, name = "tcp"),
        @JsonSubTypes.Type(value = HttpHealthCheck.class, name = "http"),
        @JsonSubTypes.Type(value = TcpPayloadHealthCheck.class, name = "tcppayload"),
        @JsonSubTypes.Type(value = CommandHealthCheck.class, name = "command")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class HealthCheck extends JsonRepresentable {

    private final String id;

    /**
     * Create a HealthCheck
     *
     * @param id   the id of the healthcheck
     * @param type the type of the healthcheck
     */
    @Contract(pure = true)
    public HealthCheck(
            @JsonProperty("id") final String id,
            @JsonProperty("type") final String type) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
