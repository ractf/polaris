package uk.co.ractf.polaris.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.ractf.polaris.api.notification.Notification;
import uk.co.ractf.polaris.api.notification.SlackWebhook;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;

public class SlackNotifier implements Notifier {

    private final SlackWebhook slackWebhook;

    public SlackNotifier(final SlackWebhook slackWebhook) {
        this.slackWebhook = slackWebhook;
    }

    @Override
    public void notify(final Notification notification) {
        try {
            final var webhook = new HashMap<String, Object>();
            webhook.put("username", "Polaris");
            final var embed = new HashMap<String, Object>();
            embed.put("color", notification.getLevel().getColour());
            if (notification.getNamespace() == null) {
                embed.put("title", notification.getSummary());
            } else {
                embed.put("title", notification.getSummary() + " (`" + notification.getNamespace().getName() + "`)");
            }
            embed.put("text", notification.getDetail());
            webhook.put("attachments", Collections.singletonList(embed));
            final var json = new ObjectMapper().writeValueAsString(webhook);
            final var httpClient = HttpClient.newBuilder().build();
            final var request = HttpRequest.newBuilder()
                    .uri(URI.create(slackWebhook.getWebhookUrl()))
                    .method("POST", HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();
            final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (final IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
