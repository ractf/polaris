package uk.co.ractf.polaris.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.challenge.ChallengeDeleteResponse;
import uk.co.ractf.polaris.api.challenge.ChallengeSubmitResponse;
import uk.co.ractf.polaris.api.pod.Pod;

import java.util.Collections;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

    @Test
    public void testChallengeEquals() {
        EqualsVerifier.simple().forClass(Challenge.class).verify();
    }

    @Test
    public void testChallengeDeleteResponseEquals() {
        EqualsVerifier.simple().forClass(ChallengeDeleteResponse.class).verify();
    }

    @Test
    public void testChallengeSubmitResponseEquals() {
        EqualsVerifier.simple().forClass(ChallengeSubmitResponse.class).verify();
    }

    @Test
    public void testGetPodById() {
        final Pod pod = mock(Pod.class);
        when(pod.getId()).thenReturn("test");
        final Challenge challenge = new Challenge("test", Collections.singletonList(pod));
        assertThat(challenge.getPod("test")).isEqualTo(pod);
    }

    @Test
    public void testGetPodByIdReturnsNull() {
        final Pod pod = mock(Pod.class);
        when(pod.getId()).thenReturn("test");
        final Challenge challenge = new Challenge("test", Collections.singletonList(pod));
        assertThat(challenge.getPod("test2")).isNull();
    }

}
