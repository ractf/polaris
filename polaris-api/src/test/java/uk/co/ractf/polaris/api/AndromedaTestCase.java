package uk.co.ractf.polaris.api;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.andromeda.*;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static uk.co.ractf.polaris.testlib.JacksonTester.testObject;

public class AndromedaTestCase {

    @Test
    public void testAndromedaAuthentication() {
        testObject(AndromedaAuthentication.class, fixture("fixtures/andromeda/authentication.json"));
    }

    @Test
    public void testAndromedaChallenge() {
        testObject(AndromedaChallenge.class, fixture("fixtures/andromeda/challenge.json"));
    }

    @Test
    public void testInstance() {
        testObject(AndromedaInstance.class, fixture("fixtures/andromeda/instance.json"));
    }

    @Test
    public void testInstanceRequest() {
        testObject(AndromedaInstanceRequest.class, fixture("fixtures/andromeda/instancerequest.json"));
    }

    @Test
    public void testResources() {
        testObject(AndromedaResources.class, fixture("fixtures/andromeda/resources.json"));
    }

    @Test
    public void testSubmitResponse() {
        testObject(AndromedaChallengeSubmitResponse.class, fixture("fixtures/andromeda/submitresponse.json"));
    }

}
