package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

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
     * @param regex   the regex to match
     * @param encoded true if regex should be matched against the hex
     */
    @Contract(pure = true)
    public ReceiveRegexTcpPayload(
            @JsonProperty("regex") final String regex,
            @JsonProperty("encoded") final Boolean encoded) {
        this.regex = regex;
        this.encoded = encoded;
    }

    public String getRegex() {
        return regex;
    }

    public Boolean getEncoded() {
        return encoded;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ReceiveRegexTcpPayload that = (ReceiveRegexTcpPayload) o;
        return Objects.equals(regex, that.regex) && Objects.equals(encoded, that.encoded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regex, encoded);
    }
}
