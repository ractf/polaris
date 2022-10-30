package uk.co.ractf.polaris.api.registry.credentials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.namespace.NamespacedId;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CredentialsSubmitResponse extends JsonRepresentable {

    private final Status status;
    private final NamespacedId namespacedId;
    public CredentialsSubmitResponse(final Status status, final NamespacedId namespacedId) {
        this.status = status;
        this.namespacedId = namespacedId;
    }

    public Status getStatus() {
        return status;
    }

    public NamespacedId getNamespacedId() {
        return namespacedId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CredentialsSubmitResponse)) return false;
        final CredentialsSubmitResponse that = (CredentialsSubmitResponse) o;
        return status == that.status && Objects.equals(namespacedId, that.namespacedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, namespacedId);
    }

    public enum Status {
        OK, DUPLICATE, INVALID, BAD_NAMESPACE, FORBIDDEN_NAMESPACE;
    }
}
