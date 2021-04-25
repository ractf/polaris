package uk.co.ractf.polaris.api;

import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.challenge.ChallengeDeleteResponse;
import uk.co.ractf.polaris.api.challenge.ChallengeSubmitResponse;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObject;
import static uk.co.ractf.polaris.testlib.JacksonTester.validateObjectIgnoreProperties;

public class ChallengeTestCase {

    @Test
    public void testChallenge() {
        validateObject(Challenge.class, fixture("fixtures/challenge/challenge.json"));
    }

    @Test
    public void testChallengeDeleteResponse() {
        validateObject(ChallengeDeleteResponse.class, fixture("fixtures/challenge/deleteresponse.json"));
    }

    @Test
    public void testChallengeSubmitResponse() {
        validateObject(ChallengeSubmitResponse.class, fixture("fixtures/challenge/submitresponse.json"));
    }

    @Test
    public void testChallengeIgnoreProperties() {
        validateObjectIgnoreProperties(Challenge.class, fixture("fixtures/challenge/challenge.json"));
    }

    @Test
    public void testChallengeDeleteResponseIgnoreProperties() {
        validateObjectIgnoreProperties(ChallengeDeleteResponse.class, fixture("fixtures/challenge/deleteresponse.json"));
    }

    @Test
    public void testChallengeSubmitResponseIgnoreProperties() {
        validateObjectIgnoreProperties(ChallengeSubmitResponse.class, fixture("fixtures/challenge/submitresponse.json"));
    }

}
