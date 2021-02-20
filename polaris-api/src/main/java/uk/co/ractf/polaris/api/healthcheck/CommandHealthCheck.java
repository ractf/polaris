package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandHealthCheck extends HealthCheck {

    private final String command;
    private final Integer returnValue;

    public CommandHealthCheck(
            @JsonProperty("id") final String id,
            @JsonProperty("type") final String type,
            @JsonProperty("command") final String command,
            @JsonProperty("returnValue") final Integer returnValue) {
        super(id, type);
        this.command = command;
        this.returnValue = returnValue;
    }

    public String getCommand() {
        return command;
    }

    public Integer getReturnValue() {
        return returnValue;
    }
}
