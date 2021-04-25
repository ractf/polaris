package uk.co.polaris.testlib;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InvalidObject {

    private final String id;
    private final String name;

    public InvalidObject(
            @JsonProperty("id") final String id,
            @JsonProperty("id") final String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
