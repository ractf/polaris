package uk.co.ractf.polaris.api.andromeda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.List;
import java.util.Objects;

/**
 * Represents the instanceDetails struct from Andromeda, sent to the user when they request or reset an instance
 */
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
public class AndromedaInstance extends JsonRepresentable {

    private final String ip;
    private final Integer port;
    private final List<String> extra;

    public AndromedaInstance(
            @JsonProperty("ip") final String ip,
            @JsonProperty("port") final Integer port,
            @JsonProperty("extra") final List<String> extra) {
        this.ip = ip;
        this.port = port;
        this.extra = extra;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public List<String> getExtra() {
        return extra;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof AndromedaInstance)) return false;
        final AndromedaInstance that = (AndromedaInstance) o;
        return Objects.equals(ip, that.ip) && Objects.equals(port, that.port) && Objects.equals(extra, that.extra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, extra);
    }
}
