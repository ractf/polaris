package uk.co.ractf.polaris.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.instanceallocation.InstanceRequest;
import uk.co.ractf.polaris.api.instanceallocation.InstanceResponse;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObject;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObjectIgnoreProperties;

public class InstanceAllocationTestCase {

    @Test
    public void testInstanceRequest() {
        validateObject(InstanceRequest.class, fixture("fixtures/instanceallocation/request.json"));
    }

    @Test
    public void testInstanceResponse() {
        validateObject(InstanceResponse.class, fixture("fixtures/instanceallocation/response.json"));
    }

    @Test
    public void testInstanceRequestIgnoreProperties() {
        validateObjectIgnoreProperties(InstanceRequest.class, fixture("fixtures/instanceallocation/request.json"));
    }

    @Test
    public void testInstanceResponseIgnoreProperties() {
        validateObjectIgnoreProperties(InstanceResponse.class, fixture("fixtures/instanceallocation/response.json"));
    }

    @Test
    public void testInstanceRequestEquals() {
        EqualsVerifier.simple().forClass(InstanceRequest.class).verify();
    }

    @Test
    public void testInstanceResponseEquals() {
        EqualsVerifier.simple().forClass(InstanceResponse.class).verify();
    }
}
