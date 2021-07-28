package uk.co.ractf.polaris.api.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.namespace.NamespacedId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackWebhook extends NotificationReceiver {

    private final String webhookUrl;

    public SlackWebhook(
            @JsonProperty("id") final NamespacedId id,
            @JsonProperty("minimumLevel") final NotificationLevel minimumLevel,
            @JsonProperty("system") final boolean system,
            @JsonProperty("global") final boolean global,
            @JsonProperty("webhookUrl") final String webhookUrl) {
        super(id, minimumLevel, system, global);
        this.webhookUrl = webhookUrl;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }
}
