package uk.co.ractf.polaris.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.pod.PortMapping;
import uk.co.ractf.polaris.api.pod.ResourceQuota;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObject;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObjectIgnoreProperties;

public class PodTestCase {

    @Test
    public void testContainer() {
        validateObject(Container.class, fixture("fixtures/pod/container.json"));
    }

    @Test
    public void testPortMapping() {
        validateObject(PortMapping.class, fixture("fixtures/pod/port.json"));
    }

    @Test
    public void testResourceQuota() {
        validateObject(ResourceQuota.class, fixture("fixtures/pod/resources.json"));
    }

    @Test
    public void testContainerIgnoreProperties() {
        validateObjectIgnoreProperties(Container.class, fixture("fixtures/pod/container.json"));
    }

    @Test
    public void testPortMappingIgnoreProperties() {
        validateObjectIgnoreProperties(PortMapping.class, fixture("fixtures/pod/port.json"));
    }

    @Test
    public void testResourceQuotaIgnoreProperties() {
        validateObjectIgnoreProperties(ResourceQuota.class, fixture("fixtures/pod/resources.json"));
    }

    @Test
    public void testContainerEquals() {
        EqualsVerifier.simple().forClass(Container.class).verify();
    }

    @Test
    public void testPortMappingEquals() {
        EqualsVerifier.simple().forClass(PortMapping.class).verify();
    }

    @Test
    public void testResourceQuotaEquals() {
        EqualsVerifier.simple().forClass(ResourceQuota.class).verify();
    }

    @Test
    public void testGetFullEnv() throws JsonProcessingException {
        final Container container = Container.parse(fixture("fixtures/pod/container.json"), Container.class);
        assertThat(container.getFullEnv()).hasSize(3);
    }

    @Test
    public void testGetRandomEnv() throws JsonProcessingException {
        final Container container = Container.parse(fixture("fixtures/pod/container.json"), Container.class);
        assertThat(container.getGeneratedRandomEnv()).hasSize(2);
    }

    @Test
    public void testGetRandomEnvDoesntRegenerate() throws JsonProcessingException {
        final Container container = Container.parse(fixture("fixtures/pod/container.json"), Container.class);
        assertThat(container.getGeneratedRandomEnv()).isEqualTo(container.getGeneratedRandomEnv());
    }


}
