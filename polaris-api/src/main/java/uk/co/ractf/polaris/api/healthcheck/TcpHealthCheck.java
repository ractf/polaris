package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
     * @param id the id of the healthcheck
     * @param type the type of the healthcheck (tcp)
     * @param port the port to connect to
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

}
