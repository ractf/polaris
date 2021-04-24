package uk.co.ractf.polaris.api.common;

import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class JsonRepresentable {

    public static <T extends JsonRepresentable> T parse(final String json, Class<T> clazz) throws JsonProcessingException {
        return Json.read(json, clazz);
    }

    public String toJsonString() {
        try {
            return Json.asString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
