package uk.co.ractf.polaris.api.andromeda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
public class AndromedaChallengeSubmitResponse {

    private final String id;

    public AndromedaChallengeSubmitResponse(@JsonProperty("id") final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
