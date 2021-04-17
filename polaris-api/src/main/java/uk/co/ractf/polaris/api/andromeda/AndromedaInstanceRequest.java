package uk.co.ractf.polaris.api.andromeda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

/**
 * Represents the format in which instance's are requested and resets requested from andromeda.
 * {
 *     "user": "123",
 *     "job": "challenge"
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AndromedaInstanceRequest extends JsonRepresentable {

    private final String job;
    private final String user;

    public AndromedaInstanceRequest(
            @JsonProperty("job") final String job,
            @JsonProperty("user") final String user) {
        this.job = job;
        this.user = user;
    }

    public String getJob() {
        return job;
    }

    public String getUser() {
        return user;
    }
}
