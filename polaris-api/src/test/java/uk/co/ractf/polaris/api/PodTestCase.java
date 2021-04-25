package uk.co.ractf.polaris.api;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.pod.PortMapping;
import uk.co.ractf.polaris.api.pod.ResourceQuota;

import static io.dropwizard.testing.FixtureHelpers.fixture;
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

}
