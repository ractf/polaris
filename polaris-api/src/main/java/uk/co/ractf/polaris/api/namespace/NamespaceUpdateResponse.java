package uk.co.ractf.polaris.api.namespace;

import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.Objects;

public class NamespaceUpdateResponse extends JsonRepresentable {

    private final Status status;
    private final String name;
    public NamespaceUpdateResponse(final Status status, final String name) {
        this.status = status;
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof NamespaceUpdateResponse)) return false;
        final NamespaceUpdateResponse that = (NamespaceUpdateResponse) o;
        return status == that.status && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, name);
    }

    public enum Status {
        OK, NOT_FOUND;
    }
}