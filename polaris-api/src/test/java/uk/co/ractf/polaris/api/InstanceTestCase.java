package uk.co.ractf.polaris.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instance.InstancePortBinding;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    public void testInstanceEquals() {
        EqualsVerifier.simple().forClass(Instance.class).verify();
    }

    @Test
    public void testPortEquals() {
        EqualsVerifier.simple().forClass(InstancePortBinding.class).verify();
    }

    @Test
    public void testAddPortBinding() throws JsonProcessingException {
        final Instance instance = Instance.parse(fixture("fixtures/instance/instance.json"), Instance.class);
        instance.addPortBinding(new InstancePortBinding("69", "0.0.0.0", true));
        assertThat(instance.getPortBindings()).size().isEqualTo(1);
    }

}
