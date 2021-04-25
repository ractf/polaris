package uk.co.ractf.polaris.testlib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonTester {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> void testObject(final Class<T> clazz, final String fixture) {
        try {
            final T t = objectMapper.readValue(fixture, clazz);
            final String result = objectMapper.writeValueAsString(t);
            assertEquals(objectMapper.readTree(result), objectMapper.readTree(fixture));
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
