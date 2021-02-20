package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiveRegexTcpPayload extends TcpPayload {

    private final String regex;
    private final Boolean encoded;

    public ReceiveRegexTcpPayload(
            @JsonProperty("type") final String type,
            @JsonProperty("regex") final String regex,
            @JsonProperty("encoded") final Boolean encoded) {
        super(type);
        this.regex = regex;
        this.encoded = encoded;
    }

    public String getRegex() {
        return regex;
    }

    public Boolean getEncoded() {
        return encoded;
    }

}
