package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpHealthCheck extends HealthCheck {

    private final String path;
    private final String port;
    private final String vhost;

    public HttpHealthCheck(
            @JsonProperty("id") final String id,
            @JsonProperty("type") final String type,
            @JsonProperty("path") final String path,
            @JsonProperty("port") final String port,
            @JsonProperty("vhost") final String vhost) {
        super(id, type);
        this.path = path;
        this.port = port;
        this.vhost = vhost;
    }

    public String getPath() {
        return path;
    }

    public String getPort() {
        return port;
    }

    public String getVhost() {
        return vhost;
    }

}
