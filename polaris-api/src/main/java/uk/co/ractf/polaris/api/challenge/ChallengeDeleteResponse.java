package uk.co.ractf.polaris.api.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeDeleteResponse extends JsonRepresentable {

    public enum Status {
        SUCCESS, NOT_FOUND;
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
}
