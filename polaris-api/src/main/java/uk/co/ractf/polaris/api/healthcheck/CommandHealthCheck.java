package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

/**
 * A {@link HealthCheck} that runs a command inside a pod, returns healthy if the given return value is returned.
 *
 * <pre>
 *     {
 *         "id": "cmdhealth1",
 *         "type": "command",
 *         "command": "ls -la",
 *         "returnValue": 0
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandHealthCheck extends HealthCheck {

    private final String command;
    private final Integer returnValue;

    /**
     * Create a CommandHealthCheck
     *
     * @param id          the id of the healthcheck
     * @param type        the type of the healthcheck (command)
     * @param command     the command to run
     * @param returnValue what that command should return if healthy
     */
    @Contract(pure = true)
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandHealthCheck)) return false;
        final CommandHealthCheck that = (CommandHealthCheck) o;
        return Objects.equals(command, that.command) && Objects.equals(returnValue, that.returnValue) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, returnValue, getId());
    }
}
