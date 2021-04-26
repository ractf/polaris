package uk.co.ractf.polaris.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class JsonRepresentableTestCase {

    @Test
    public void testJsonRepresentable() throws JsonProcessingException {
        final TestJsonRepresentable obj = TestJsonRepresentable.parse("{\"a\":\"a\", \"b\":\"b\"}", TestJsonRepresentable.class);
        final ObjectMapper objectMapper = new ObjectMapper();
        assertThat(objectMapper.readTree("{\"a\":\"a\", \"b\":\"b\"}")).isEqualTo(objectMapper.readTree(obj.toJsonString()));
    }

    @Test
    public void testExceptionThrown() throws JsonProcessingException {
        final TestJsonRepresentable obj = TestJsonRepresentable.parse("{\"a\":\"a\", \"b\":\"b\"}", TestJsonRepresentable.class);
        final ObjectMapper objectMapper = new ObjectMapper();
        assertThat(objectMapper.readTree("{\"a\":\"a\", \"b\":\"b\"}")).isEqualTo(objectMapper.readTree(obj.toJsonString()));
    }

}
