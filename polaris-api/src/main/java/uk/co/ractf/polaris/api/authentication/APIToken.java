package uk.co.ractf.polaris.api.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class APIToken extends JsonRepresentable {

    private final String id;
    private final String token;
    private final List<String> allowedNamespaces;
    private final List<String> roles;

    public APIToken(
            @JsonProperty("name") final String id,
            @JsonProperty("token") final String token,
            @JsonProperty("allowedNamespaces") final List<String> allowedNamespaces,
            @JsonProperty("permissions") final List<String> roles) {
        this.id = id;
        this.token = token;
        this.allowedNamespaces = allowedNamespaces;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public List<String> getAllowedNamespaces() {
        return allowedNamespaces;
    }

    public List<String> getRoles() {
        return roles;
    }
}
