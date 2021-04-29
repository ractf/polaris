package uk.co.ractf.polaris.api.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeDeleteResponse extends JsonRepresentable {

    public enum Status {
        OK, NOT_FOUND;
    }

    private final Status status;
    private final String id;

    public ChallengeDeleteResponse(
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
        if (!(o instanceof ChallengeDeleteResponse)) return false;
        final ChallengeDeleteResponse that = (ChallengeDeleteResponse) o;
        return status == that.status && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, id);
    }
}
