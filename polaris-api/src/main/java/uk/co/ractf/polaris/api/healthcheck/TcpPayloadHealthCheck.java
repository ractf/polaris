package uk.co.ractf.polaris.api.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Objects;

/**
 * Represents a healthcheck that checks a tcp service is healthy using a call/response sequence of payloads
 *
 * <pre>
 *     {
 *         "id": "tcppayloadcheck",
 *         "type": "tcppayload",
 *         "payloads": [
 *             {
 *                 "type": "send",
 *                 "hex": "1234567890"
 *             },
 *             {
 *                 "type": "receiveregex",
 *                 "regex": ".{6}",
 *                 "encoded": true
 *             }
 *         ],
 *         "connectionTimeout": 5,
 *         "sequenceTimeout": 15
 *     }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TcpPayloadHealthCheck extends HealthCheck {

    private final List<TcpPayload> payloads;
    private final Integer connectionTimeout;
    private final Integer sequenceTimeout;

    /**
     * Create a TcpPayloadHealthCheck
     *
     * @param id                id of the healthcheck
     * @param type              type of the healthcheck (tcppayload)
     * @param payloads          the payloads to send/receive
     * @param connectionTimeout the timeout on the initial connection
     * @param sequenceTimeout   the timeout on the whole sequence
     */
    @Contract(pure = true)
    public TcpPayloadHealthCheck(
            @JsonProperty("id") final String id,
            @JsonProperty("type") final String type,
            @JsonProperty("payloads") final List<TcpPayload> payloads,
            @JsonProperty("connectionTimeout") final Integer connectionTimeout,
            @JsonProperty("sequenceTimeout") final Integer sequenceTimeout) {
        super(id, type);
        this.payloads = payloads;
        this.connectionTimeout = connectionTimeout;
        this.sequenceTimeout = sequenceTimeout;
    }

    public List<TcpPayload> getPayloads() {
        return payloads;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public Integer getSequenceTimeout() {
        return sequenceTimeout;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TcpPayloadHealthCheck that = (TcpPayloadHealthCheck) o;
        return Objects.equals(payloads, that.payloads) && Objects.equals(connectionTimeout, that.connectionTimeout) && Objects.equals(sequenceTimeout, that.sequenceTimeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payloads, connectionTimeout, sequenceTimeout);
    }
}
