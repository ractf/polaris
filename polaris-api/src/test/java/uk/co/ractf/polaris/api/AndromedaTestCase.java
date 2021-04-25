package uk.co.ractf.polaris.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.andromeda.*;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObject;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObjectIgnoreProperties;

public class AndromedaTestCase {

    @Test
    public void testAndromedaAuthentication() {
        validateObject(AndromedaAuthentication.class, fixture("fixtures/andromeda/authentication.json"));
    }

    @Test
    public void testAndromedaChallenge() {
        validateObject(AndromedaChallenge.class, fixture("fixtures/andromeda/challenge.json"));
    }

    @Test
    public void testInstance() {
        validateObject(AndromedaInstance.class, fixture("fixtures/andromeda/instance.json"));
    }

    @Test
    public void testInstanceRequest() {
        validateObject(AndromedaInstanceRequest.class, fixture("fixtures/andromeda/instancerequest.json"));
    }

    @Test
    public void testResources() {
        validateObject(AndromedaResources.class, fixture("fixtures/andromeda/resources.json"));
    }

    @Test
    public void testSubmitResponse() {
        validateObject(AndromedaChallengeSubmitResponse.class, fixture("fixtures/andromeda/submitresponse.json"));
    }

    @Test
    public void testAndromedaAuthenticationIgnoreProperties() {
        validateObjectIgnoreProperties(AndromedaAuthentication.class, fixture("fixtures/andromeda/authentication.json"));
    }

    @Test
    public void testAndromedaChallengeIgnoreProperties() {
        validateObjectIgnoreProperties(AndromedaChallenge.class, fixture("fixtures/andromeda/challenge.json"));
    }

    @Test
    public void testInstanceIgnoreProperties() {
        validateObjectIgnoreProperties(AndromedaInstance.class, fixture("fixtures/andromeda/instance.json"));
    }

    @Test
    public void testInstanceRequestIgnoreProperties() {
        validateObjectIgnoreProperties(AndromedaInstanceRequest.class, fixture("fixtures/andromeda/instancerequest.json"));
    }

    @Test
    public void testResourcesIgnoreProperties() {
        validateObjectIgnoreProperties(AndromedaResources.class, fixture("fixtures/andromeda/resources.json"));
    }

    @Test
    public void testSubmitResponseIgnoreProperties() {
        validateObjectIgnoreProperties(AndromedaChallengeSubmitResponse.class, fixture("fixtures/andromeda/submitresponse.json"));
    }

    @Test
    public void testAndromedaAuthenticationEquals() {
        EqualsVerifier.simple().forClass(AndromedaAuthentication.class).verify();
    }

    @Test
    public void testAndromedaChallengeEquals() {
        EqualsVerifier.simple().forClass(AndromedaChallenge.class).verify();
    }

    @Test
    public void testInstanceEquals() {
        EqualsVerifier.simple().forClass(AndromedaInstance.class).verify();
    }

    @Test
    public void testInstanceRequestEquals() {
        EqualsVerifier.simple().forClass(AndromedaInstanceRequest.class).verify();
    }

    @Test
    public void testResourcesEquals() {
        EqualsVerifier.simple().forClass(AndromedaResources.class).verify();
    }

    @Test
    public void testSubmitResponseEquals() {
        EqualsVerifier.simple().forClass(AndromedaChallengeSubmitResponse.class).verify();
    }

}
