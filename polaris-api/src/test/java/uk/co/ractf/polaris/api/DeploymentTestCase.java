package uk.co.ractf.polaris.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.deployment.Allocation;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.deployment.StaticReplication;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObject;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObjectIgnoreProperties;

public class DeploymentTestCase {

    @Test
    public void testAllocation() {
        validateObject(Allocation.class, fixture("fixtures/deployment/allocation.json"));
    }

    @Test
    public void testDeployment() {
        validateObject(Deployment.class, fixture("fixtures/deployment/deployment.json"));
    }

    @Test
    public void testStaticReplication() {
        validateObject(StaticReplication.class, fixture("fixtures/deployment/staticreplication.json"));
    }

    @Test
    public void testAllocationIgnoreProperties() {
        validateObjectIgnoreProperties(Allocation.class, fixture("fixtures/deployment/allocation.json"));
    }

    @Test
    public void testDeploymentIgnoreProperties() {
        validateObjectIgnoreProperties(Deployment.class, fixture("fixtures/deployment/deployment.json"));
    }

    @Test
    public void testStaticReplicationIgnoreProperties() {
        validateObjectIgnoreProperties(StaticReplication.class, fixture("fixtures/deployment/staticreplication.json"));
    }

    @Test
    public void testAllocationEquals() {
        EqualsVerifier.simple().forClass(Allocation.class).verify();
    }

    @Test
    public void testDeploymentEquals() {
        EqualsVerifier.simple().forClass(Deployment.class).verify();
    }

    @Test
    public void testStaticReplicationEquals() {
        EqualsVerifier.simple().forClass(StaticReplication.class).verify();
    }

}
