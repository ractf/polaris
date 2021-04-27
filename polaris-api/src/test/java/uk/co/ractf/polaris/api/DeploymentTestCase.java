package uk.co.ractf.polaris.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.deployment.*;

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
    public void testDeleteResponse() {
        validateObject(DeploymentDeleteResponse.class, fixture("fixtures/deployment/deleteresponse.json"));
    }

    @Test
    public void testSubmitResponse() {
        validateObject(DeploymentSubmitResponse.class, fixture("fixtures/deployment/submitresponse.json"));
    }

    @Test
    public void testUpdateResponse() {
        validateObject(DeploymentUpdateResponse.class, fixture("fixtures/deployment/updateresponse.json"));
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
    public void testDeleteResponseIgnoreProperties() {
        validateObjectIgnoreProperties(DeploymentDeleteResponse.class, fixture("fixtures/deployment/deleteresponse.json"));
    }

    @Test
    public void testSubmitResponseIgnoreProperties() {
        validateObjectIgnoreProperties(DeploymentSubmitResponse.class, fixture("fixtures/deployment/submitresponse.json"));
    }

    @Test
    public void testUpdateResponseIgnoreProperties() {
        validateObjectIgnoreProperties(DeploymentUpdateResponse.class, fixture("fixtures/deployment/updateresponse.json"));
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

    @Test
    public void testDeleteResponseEquals() {
        EqualsVerifier.simple().forClass(DeploymentDeleteResponse.class).verify();
    }

    @Test
    public void testSubmitResponseEquals() {
        EqualsVerifier.simple().forClass(DeploymentSubmitResponse.class).verify();
    }

    @Test
    public void testUpdateResponseEquals() {
        EqualsVerifier.simple().forClass(DeploymentUpdateResponse.class).verify();
    }

}
