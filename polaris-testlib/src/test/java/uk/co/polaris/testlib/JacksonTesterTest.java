package uk.co.polaris.testlib;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.testlib.JacksonTester;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class JacksonTesterTest {

    @Test
    public void testValidObject() {
        JacksonTester.validateObject(ValidObject.class, "{\"name\": \"test\", \"id\":\"2\"}");
    }

    @Test
    public void testInvalidObject() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
                () -> JacksonTester.validateObject(InvalidObject.class, "{\"name\": \"test\", \"id\":\"2\"}"));
    }

}
