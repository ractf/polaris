package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiveExactTcpPayload extends TcpPayload {

    private final String hex;

    public ReceiveExactTcpPayload(
            @JsonProperty("type") final String type,
            @JsonProperty("hex") final String hex) {
        super(type);
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

}
