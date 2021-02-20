package uk.co.ractf.polaris.api.pod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortMapping {

    private final Integer port;
    private final String protocol;
    private final Boolean advertise;

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

}
