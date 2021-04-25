package uk.co.polaris.testlib;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidObject {

    private final String id;
    private final String name;

    public ValidObject(
            @JsonProperty("id") final String id,
            @JsonProperty("name") final String name) {
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
