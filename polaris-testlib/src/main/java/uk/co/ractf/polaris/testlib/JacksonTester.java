package uk.co.ractf.polaris.testlib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonTester {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> void validateObject(final Class<T> clazz, final String fixture) {
        try {
            final T t = objectMapper.readValue(fixture, clazz);
            final String result = objectMapper.writeValueAsString(t);
            assertEquals(objectMapper.readTree(result), objectMapper.readTree(fixture));
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void validateObjectIgnoreProperties(final Class<T> clazz, final String fixture) {
        try {
            final Map<String, Object> map = objectMapper.readValue(fixture, new TypeReference<>() {});
            map.put("UNKNOWN_PROPERTY_KEY", "UNKNOWN_PROPERTY_VALUE");
            final T t = objectMapper.readValue(objectMapper.writeValueAsString(map), clazz);
            final String result = objectMapper.writeValueAsString(t);
            assertEquals(objectMapper.readTree(result), objectMapper.readTree(fixture));
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
