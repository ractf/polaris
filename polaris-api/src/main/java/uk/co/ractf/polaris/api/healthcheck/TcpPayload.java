package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SendTcpPayload.class, name = "send"),
        @JsonSubTypes.Type(value = ReceiveExactTcpPayload.class, name = "receive"),
        @JsonSubTypes.Type(value = ReceiveRegexTcpPayload.class, name = "receiveregex")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TcpPayload {

    private final String type;

    public TcpPayload(final String type) {
        this.type = type;
    }

}
