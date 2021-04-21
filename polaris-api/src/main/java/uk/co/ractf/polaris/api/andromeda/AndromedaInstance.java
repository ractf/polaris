package uk.co.ractf.polaris.api.andromeda;

import uk.co.ractf.polaris.api.common.JsonRepresentable;

/**
 * Represents the instanceDetails struct from Andromeda, sent to the user when they request or reset an instance
 */
@Deprecated
public class AndromedaInstance extends JsonRepresentable {

    private final String ip;
    private final Integer port;

    public AndromedaInstance(final String ip, final Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }
}