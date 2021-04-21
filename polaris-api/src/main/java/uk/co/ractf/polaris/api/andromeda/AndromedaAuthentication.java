package uk.co.ractf.polaris.api.andromeda;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

@Deprecated
public class AndromedaAuthentication extends JsonRepresentable {

    private final String username;
    private final String password;

    public AndromedaAuthentication(
            @JsonProperty("username") final String username,
            @JsonProperty("password") final String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
