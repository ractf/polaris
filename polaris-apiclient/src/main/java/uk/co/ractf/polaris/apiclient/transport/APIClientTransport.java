package uk.co.ractf.polaris.apiclient.transport;

import com.fasterxml.jackson.core.type.TypeReference;

public interface APIClientTransport {

    <T> T get(final String route, final Class<T> clazz);

    <T, B> T post(final String route, final B body, final Class<T> clazz);

    <T, B> T put(final String route, final B body, final Class<T> clazz);

    <T, B> T patch(final String route, final B body, final Class<T> clazz);

    <T> T delete(final String route, final Class<T> clazz);

    <T> T get(final String route, final TypeReference<T> clazz);

    <T, B> T post(final String route, final B body, final TypeReference<T> clazz);

    <T, B> T put(final String route, final B body, final TypeReference<T> clazz);

    <T, B> T patch(final String route, final B body, final TypeReference<T> clazz);

    <T> T delete(final String route, final TypeReference<T> clazz);

}
