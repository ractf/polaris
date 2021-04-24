package uk.co.ractf.polaris.api.pod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.Objects;

/**
 * Represents the mapping between a port on a {@link Pod} and an external port
 *
 * <pre>
 *     {
 *         "port": 22,
 *         "protocol": "tcp",
 *         "advertise": false
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortMapping extends JsonRepresentable {

    private final Integer port;
    private final String protocol;
    private final Boolean advertise;

    /**
     * @param port the port the container uses
     * @param protocol the protocol the port is on
     * @param advertise should non admins be told about the port
     */
    @Contract(pure = true)
    public PortMapping(
            @JsonProperty("port") final Integer port,
            @JsonProperty("protocol") final String protocol,
            @JsonProperty("advertise") final Boolean advertise) {
        this.port = port;
        this.protocol = protocol;
        this.advertise = advertise;
    }

    public Integer getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public Boolean isAdvertise() {
        return advertise;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PortMapping that = (PortMapping) o;
        return Objects.equals(port, that.port) && Objects.equals(protocol, that.protocol) && Objects.equals(advertise, that.advertise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, protocol, advertise);
    }
}
