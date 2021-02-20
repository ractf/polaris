package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TcpPayloadHealthCheck extends HealthCheck {

    public TcpPayloadHealthCheck(@JsonProperty("id") final String id, @JsonProperty("type") final String type) {
        super(id, type);
    }

}
