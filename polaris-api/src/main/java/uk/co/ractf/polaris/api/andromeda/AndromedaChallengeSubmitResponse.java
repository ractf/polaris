package uk.co.ractf.polaris.api.andromeda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof AndromedaChallengeSubmitResponse)) return false;
        final AndromedaChallengeSubmitResponse that = (AndromedaChallengeSubmitResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
