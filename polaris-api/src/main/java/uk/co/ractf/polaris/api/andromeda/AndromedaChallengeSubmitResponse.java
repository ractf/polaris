package uk.co.ractf.polaris.api.andromeda;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
public class AndromedaChallengeSubmitResponse {

    private final String id;

    public AndromedaChallengeSubmitResponse(@JsonProperty("id") final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
