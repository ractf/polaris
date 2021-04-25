package uk.co.ractf.polaris.api.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.Objects;

/**
 * The ports available on an instance
 *
 * <pre>
 *     {
 *         "port": "65224/tcp",
 *         "ip": "127.0.0.1",
 *         "advertise": true
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstancePortBinding extends JsonRepresentable {

    private final String port;
    private final String ip;
    private final boolean advertise;

    /**
     * @param port      the portspec ie "1234/tcp"
     * @param ip        the ip its bound to
     * @param advertise should regular users be shown this port?
     */
    @Contract(pure = true)
    public InstancePortBinding(
            @JsonProperty("port") final String port,
            @JsonProperty("ip") final String ip,
            @JsonProperty("advertise") final boolean advertise) {
        this.port = port;
        this.ip = ip;
        this.advertise = advertise;
    }

    @JsonProperty("port")
    public String getPort() {
        return port;
    }

    @JsonProperty("ip")
    public String getIp() {
        return ip;
    }

    @JsonProperty("advertise")
    public boolean getAdvertise() {
        return advertise;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final InstancePortBinding that = (InstancePortBinding) o;
        return advertise == that.advertise && Objects.equals(port, that.port) && Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, ip, advertise);
    }
}
