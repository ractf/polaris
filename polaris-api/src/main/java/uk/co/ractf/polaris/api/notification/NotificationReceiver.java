package uk.co.ractf.polaris.api.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.co.ractf.polaris.api.common.JsonRepresentable;
import uk.co.ractf.polaris.api.namespace.NamespacedId;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SlackWebhook.class, name = "slack"),
})
public class NotificationReceiver extends JsonRepresentable {

    private final NamespacedId id;
    private final NotificationLevel minimumLevel;
    private final boolean system;
    private final boolean global;

    public NotificationReceiver(
            @JsonProperty("id") final NamespacedId id,
            @JsonProperty("minimumLevel") final NotificationLevel minimumLevel,
            @JsonProperty("system") final boolean system,
            @JsonProperty("global") final boolean global) {
        this.id = id;
        this.minimumLevel = minimumLevel;
        this.system = system;
        this.global = global;
    }

    public NamespacedId getId() {
        return id;
    }

    public NotificationLevel getMinimumLevel() {
        return minimumLevel;
    }

    public boolean isSystem() {
        return system;
    }

    public boolean isGlobal() {
        return global;
    }
}
