package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

/**
 * Part of a {@link TcpPayloadHealthCheck} sequence that expects to receive an exact set of bytes (in hex)
 *
 * <pre>
 *     {
 *         "type": "receive",
 *         "hex": "deadbeef"
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiveExactTcpPayload extends TcpPayload {

    private final String hex;

    /**
     * @param type the type of tcppayload sequence
     * @param hex the hex bytes to expect exactly
     */
    @Contract(pure = true)
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
