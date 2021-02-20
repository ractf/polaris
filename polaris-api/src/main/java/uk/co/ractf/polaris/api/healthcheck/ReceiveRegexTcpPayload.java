package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

/**
 * Part of a {@link TcpPayloadHealthCheck} sequence that expects to receive a set of bytes matching the given regex
 *
 * <pre>
 *     {
 *         "type": "receiveregex",
 *         "regex": "a.*b",
 *         "encoded": true
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiveRegexTcpPayload extends TcpPayload {

    private final String regex;
    private final Boolean encoded;

    /**
     * Create a ReceiveRegexTcpPayload
     *
     * @param type the type of tcppayload (regex)
     * @param regex the regex to match
     * @param encoded true if regex should be matched against the hex
     */
    @Contract(pure = true)
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
