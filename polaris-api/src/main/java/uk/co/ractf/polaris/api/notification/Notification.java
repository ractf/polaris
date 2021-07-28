package uk.co.ractf.polaris.api.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.namespace.Namespace;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification extends JsonRepresentable {

    private final NotificationLevel level;
    private final NotificationTarget target;
    private final Namespace namespace;
    private final String summary;
    private final String detail;

    public Notification(
            @JsonProperty("level") final NotificationLevel level,
            @JsonProperty("target") final NotificationTarget target,
            @JsonProperty("namespace") final Namespace namespace,
            @JsonProperty("summary") final String summary,
            @JsonProperty("detail") final String detail) {
        this.level = level;
        this.target = target;
        this.namespace = namespace;
        this.summary = summary;
        this.detail = detail;
    }

    public NotificationLevel getLevel() {
        return level;
    }

    public NotificationTarget getTarget() {
        return target;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public String getSummary() {
        return summary;
    }

    public String getDetail() {
        return detail;
    }
}
