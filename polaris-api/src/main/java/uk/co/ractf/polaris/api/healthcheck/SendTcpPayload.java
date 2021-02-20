package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

/**
 * Part of a {@link TcpPayloadHealthCheck} sequence that will send an exact set of bytes (in hex)
 *
 * <pre>
 *     {
 *         "type": "send",
 *         "hex": "1234567890abcdef"
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendTcpPayload extends TcpPayload {

    private final String hex;

    /**
     * Create a SendTcpPayload
     *
     * @param type the type of tcppayload (send)
     * @param hex the hex encoded bytes to send
     */
    @Contract(pure = true)
    public SendTcpPayload(
            @JsonProperty("type") final String type,
            @JsonProperty("hex") final String hex) {
        super(type);
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

}
