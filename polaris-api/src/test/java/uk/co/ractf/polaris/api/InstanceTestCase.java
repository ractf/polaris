package uk.co.ractf.polaris.api;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instance.InstancePortBinding;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObject;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObjectIgnoreProperties;

public class InstanceTestCase {

    @Test
    public void testInstance() {
        validateObject(Instance.class, fixture("fixtures/instance/instance.json"));
    }

    @Test
    public void testPort() {
        validateObject(InstancePortBinding.class, fixture("fixtures/instance/portbinding.json"));
    }
    @Test
    public void testInstanceIgnoreProperties() {
        validateObjectIgnoreProperties(Instance.class, fixture("fixtures/instance/instance.json"));
    }

    @Test
    public void testPortIgnoreProperties() {
        validateObjectIgnoreProperties(InstancePortBinding.class, fixture("fixtures/instance/portbinding.json"));
    }

}
