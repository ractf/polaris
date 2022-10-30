package uk.co.ractf.polaris.api.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationReceiverDeleteResponse extends JsonRepresentable {

    private final Status status;
    private final String id;
    public NotificationReceiverDeleteResponse(final Status status, final String id) {
        this.status = status;
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationReceiverDeleteResponse)) return false;
        final var that = (NotificationReceiverDeleteResponse) o;
        return status == that.status && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, id);
    }

    public enum Status {
        OK, FORBIDDEN_NAMESPACE, NOT_FOUND;
    }
}
