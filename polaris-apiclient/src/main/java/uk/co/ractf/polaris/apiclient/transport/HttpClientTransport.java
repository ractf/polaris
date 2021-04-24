package uk.co.ractf.polaris.apiclient.transport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HttpClientTransport implements APIClientTransport {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final String apiRoot;
    private final String authHeader;

    public HttpClientTransport(final String apiRoot, final String username, final String password) {
        this.apiRoot = apiRoot;
        authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    private String join(final String... parts) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String part : parts) {
            stringBuilder.append(part.replaceAll("/*$|^/*", ""));
            stringBuilder.append("/");
        }
        return stringBuilder.toString();
    }

    private URI getAPIRoute(final String route) {
        return URI.create(join(apiRoot, route));
    }

    private <T> T deserialize(final String body, final Class<T> clazz) {
        try {
            return objectMapper.readValue(body, clazz);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T deserialize(final String body, final TypeReference<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String request(final String route, final String method) {
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(getAPIRoute(route))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .header("Authorization", authHeader)
                .build();
        final HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

    private <B> String request(final String route, final String method, final B body) {
        final String bodyText;
        try {
            bodyText = objectMapper.writeValueAsString(body);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(getAPIRoute(route))
                .method(method, HttpRequest.BodyPublishers.ofString(bodyText))
                .header("Authorization", authHeader)
                .build();
        try {
            final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T request(final String route, final Class<T> clazz, final String method) {
        return deserialize(request(route, method), clazz);
    }

    private <T, B> T request(final String route, final Class<T> clazz, final String method, final B body) {
        return deserialize(request(route, method, body), clazz);
    }

    private <T> T request(final String route, final TypeReference<T> type, final String method) {
        return deserialize(request(route, method), type);
    }

    private <T, B> T request(final String route, final TypeReference<T> type, final String method, final B body) {
        return deserialize(request(route, method, body), type);
    }

    @Override
    public <T> T get(final String route, final Class<T> clazz) {
        return request(route, clazz, "GET");
    }

    @Override
    public <T, B> T post(final String route, final B body, final Class<T> clazz) {
        return request(route, clazz, "POST", body);
    }

    @Override
    public <T, B> T put(final String route, final B body, final Class<T> clazz) {
        return request(route, clazz, "PUT", body);
    }

    @Override
    public <T, B> T patch(final String route, final B body, final Class<T> clazz) {
        return request(route, clazz, "PATCH", body);
    }

    @Override
    public <T> T delete(final String route, final Class<T> clazz) {
        return request(route, clazz, "DELETE");
    }

    @Override
    public <T> T get(final String route, final TypeReference<T> clazz) {
        return request(route, clazz, "GET");
    }

    @Override
    public <T, B> T post(final String route, final B body, final TypeReference<T> clazz) {
        return request(route, clazz, "POST", body);
    }

    @Override
    public <T, B> T put(final String route, final B body, final TypeReference<T> clazz) {
        return request(route, clazz, "PUT", body);
    }

    @Override
    public <T, B> T patch(final String route, final B body, final TypeReference<T> clazz) {
        return request(route, clazz, "PATCH", body);
    }

    @Override
    public <T> T delete(final String route, final TypeReference<T> clazz) {
        return request(route, clazz, "DELETE");
    }

}
