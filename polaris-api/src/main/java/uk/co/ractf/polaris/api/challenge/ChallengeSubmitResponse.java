package uk.co.ractf.polaris.api.challenge;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

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
}
