package uk.co.ractf.polaris.tests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConnectionTest extends IntegrationTest {

    @Test
    public void testConnection() throws IOException, InterruptedException {
        final var client = HttpClient.newHttpClient();
        final var response = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:8080")).build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(), 404);
    }

}
