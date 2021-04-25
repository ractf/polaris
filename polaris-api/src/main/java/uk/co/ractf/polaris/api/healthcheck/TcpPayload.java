package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.Contract;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

/**
 * Represents a part of a tcp payload healthcheck sequence
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SendTcpPayload.class, name = "send"),
        @JsonSubTypes.Type(value = ReceiveExactTcpPayload.class, name = "receive"),
        @JsonSubTypes.Type(value = ReceiveRegexTcpPayload.class, name = "receiveregex")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class TcpPayload extends JsonRepresentable {

    private final String type;

    /**
     * @param type the type of payload
     */
    @Contract(pure = true)
    public TcpPayload(final String type) {
        this.type = type;
    }

}
