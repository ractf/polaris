package uk.co.ractf.polaris.api.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.ractf.polaris.api.annotation.ExcludeFromGeneratedTestReport;

public abstract class JsonRepresentable {

    public static <T extends JsonRepresentable> T parse(final String json, final Class<T> clazz) throws JsonProcessingException {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(json, clazz);
    }

    @ExcludeFromGeneratedTestReport
    public String toJsonString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
