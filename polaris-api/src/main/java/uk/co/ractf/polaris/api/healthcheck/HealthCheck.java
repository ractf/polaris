package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TcpHealthCheck.class, name = "tcp"),
        @JsonSubTypes.Type(value = HttpHealthCheck.class, name = "http"),
        @JsonSubTypes.Type(value = TcpPayloadHealthCheck.class, name = "tcppayload"),
        @JsonSubTypes.Type(value = CommandHealthCheck.class, name = "command")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class HealthCheck {

    private final String id;
    private final String type;

    public HealthCheck(
            @JsonProperty("id") final String id,
            @JsonProperty("id") final String type) {
        this.id = id;
        this.type = type;
    }

}
