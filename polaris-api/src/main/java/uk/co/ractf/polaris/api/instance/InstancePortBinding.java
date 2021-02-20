package uk.co.ractf.polaris.api.instance;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstancePortBinding {

    private final String port;
    private final String ip;
    private final boolean advertise;

    public InstancePortBinding(final String port, final String ip, final boolean advertise) {
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

    @JsonProperty("advertised")
    public boolean getAdvertise() {
        return advertise;
    }
}
