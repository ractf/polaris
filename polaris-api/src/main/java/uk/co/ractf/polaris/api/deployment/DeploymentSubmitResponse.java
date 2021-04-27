package uk.co.ractf.polaris.api.deployment;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

import java.util.Objects;

public class DeploymentSubmitResponse extends JsonRepresentable {

    public enum Status {
        OK, DUPLICATE, INVALID, REJECTED;
    }

    private final String id;
    private final Status status;

    public DeploymentSubmitResponse(
            @JsonProperty("id") final String id,
            @JsonProperty("status") final Status status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DeploymentSubmitResponse)) return false;
        final DeploymentSubmitResponse that = (DeploymentSubmitResponse) o;
        return Objects.equals(id, that.id) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status);
    }

}
