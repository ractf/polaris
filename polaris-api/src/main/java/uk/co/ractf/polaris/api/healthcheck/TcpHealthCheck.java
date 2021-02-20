package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TcpHealthCheck extends HealthCheck {

    private final Integer port;
    private final Integer timeout;

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
