package uk.co.ractf.polaris.api.registry.credentials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.namespace.NamespacedId;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CredentialsUpdateResponse extends JsonRepresentable {

    public enum Status {
        OK, NOT_FOUND, INVALID, BAD_NAMESPACE, FORBIDDEN_NAMESPACE;
    }

    private final Status status;
    private final NamespacedId id;

    public CredentialsUpdateResponse(
            @JsonProperty("status") final Status status,
            @JsonProperty("id") final NamespacedId id) {
        this.status = status;
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public NamespacedId getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CredentialsUpdateResponse)) return false;
        final var that = (CredentialsUpdateResponse) o;
        return status == that.status && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, id);
    }
    
}
