package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

/**
 * Represents a HTTP healthcheck that checks that a http request to the container on a given port with a specified
 * path and vhost returns the given status code
 * <pre>
 *     {
 *         "id": "http1",
 *         "type": "http",
 *         "path": "/",
 *         "port": 8000,
 *         "vhost": "ractf.co.uk",
 *         "statusCode": 404
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpHealthCheck extends HealthCheck {

    private final String path;
    private final Integer port;
    private final String vhost;
    private final Integer statusCode;

    /**
     * Create a HttpHealthCheck
     *
     * @param id         the id of the health check
     * @param type       the type of the health check (http)
     * @param path       the path to make a request on
     * @param port       the port to make a request on
     * @param vhost      the vhost to specify
     * @param statusCode the status code to expect
     */
    @Contract(pure = true)
    public HttpHealthCheck(
            @JsonProperty("id") final String id,
            @JsonProperty("type") final String type,
            @JsonProperty("path") final String path,
            @JsonProperty("port") final Integer port,
            @JsonProperty("vhost") final String vhost,
            @JsonProperty("statusCode") final Integer statusCode) {
        super(id, type);
        this.path = path;
        this.port = port;
        this.vhost = vhost;
        this.statusCode = statusCode;
    }

    public String getPath() {
        return path;
    }

    public Integer getPort() {
        return port;
    }

    public String getVhost() {
        return vhost;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HttpHealthCheck that = (HttpHealthCheck) o;
        return Objects.equals(path, that.path) && Objects.equals(port, that.port) && Objects.equals(vhost, that.vhost) && Objects.equals(statusCode, that.statusCode) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, port, vhost, statusCode, getId());
    }
}
