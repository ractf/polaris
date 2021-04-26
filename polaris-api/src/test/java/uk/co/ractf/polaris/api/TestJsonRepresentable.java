package uk.co.ractf.polaris.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ractf.polaris.api.common.JsonRepresentable;

public class TestJsonRepresentable extends JsonRepresentable {

    private final String a;
    private final String b;

    public TestJsonRepresentable(
            @JsonProperty("a") final String a,
            @JsonProperty("b") final String b) {
        this.a = a;
        this.b = b;
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }
}
