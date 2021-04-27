package uk.co.ractf.polaris.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.node.PortAllocations;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObject;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObjectIgnoreProperties;

public class NodeTestCase {

    @Test
    public void testNodeInfo() {
        validateObject(NodeInfo.class, fixture("fixtures/node/info.json"));
    }

    @Test
    public void testNodeInfoIgnoreProperties() {
        validateObjectIgnoreProperties(NodeInfo.class, fixture("fixtures/node/info.json"));
    }

    @Test
    public void testNodeInfoEquals() {
        EqualsVerifier.simple().forClass(NodeInfo.class).verify();
    }

    @Test
    public void testPortAllocations() {
        validateObject(PortAllocations.class, fixture("fixtures/node/ports.json"));
    }

    @Test
    public void testPortAllocationsIgnoreProperties() {
        validateObjectIgnoreProperties(PortAllocations.class, fixture("fixtures/node/ports.json"));
    }

    @Test
    public void testPortAllocationsEquals() {
        EqualsVerifier.simple().forClass(PortAllocations.class).verify();
    }

}
