package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a healthcheck that checks a tcp port is open with a given timeout
 *
 * <pre>
 *     {
 *         "id": "tcpconnect",
 *         "type": "tcp",
 *         "port": 6000,
 *         "timeout": 15
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TcpHealthCheck extends HealthCheck {

    private final Integer port;
    private final Integer timeout;

    /**
     * @param id      the id of the healthcheck
     * @param type    the type of the healthcheck (tcp)
     * @param port    the port to connect to
     * @param timeout the connection timeout
     */
    public TcpHealthCheck(
            @JsonProperty("id") final String id,
            @JsonProperty("type") final String type,
            @JsonProperty("port") final Integer port,
            @JsonProperty("timeout") final Integer timeout) {
        super(id, type);
        this.port = port;
        this.timeout = timeout;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TcpHealthCheck that = (TcpHealthCheck) o;
        return Objects.equals(port, that.port) && Objects.equals(timeout, that.timeout) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, timeout, getId());
    }
}
