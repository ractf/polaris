package uk.co.ractf.polaris.api.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeSubmitResponse extends JsonRepresentable {

    public enum Status {
        OK, DUPLICATE, INVALID;
    }

    private final Status status;
    private final String id;

    public ChallengeSubmitResponse(
            @JsonProperty("status") final Status status,
            @JsonProperty("id") final String id) {
        this.status = status;
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ChallengeSubmitResponse)) return false;
        final ChallengeSubmitResponse response = (ChallengeSubmitResponse) o;
        return status == response.status && Objects.equals(id, response.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, id);
    }
}
