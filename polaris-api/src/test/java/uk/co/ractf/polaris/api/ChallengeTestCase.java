package uk.co.ractf.polaris.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.api.task.TaskId;

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
    public void testChallengeIgnoreProperties() {
        validateObjectIgnoreProperties(Challenge.class, fixture("fixtures/challenge/challenge.json"));
    }

    @Test
    public void testChallengeEquals() {
        EqualsVerifier.simple().forClass(Challenge.class).verify();
    }

    @Test
    public void testGetPodById() {
        final var pod = mock(Pod.class);
        when(pod.getId()).thenReturn("test");
        final var challenge = new Challenge(new TaskId("test"), 0, Collections.singletonList(pod), null, null);
        assertThat(challenge.getPod("test")).isEqualTo(pod);
    }

    @Test
    public void testGetPodByIdReturnsNull() {
        final var pod = mock(Pod.class);
        when(pod.getId()).thenReturn("test");
        final var challenge = new Challenge(new TaskId("test"), 0, Collections.singletonList(pod), null, null);
        assertThat(challenge.getPod("test2")).isNull();
    }

}
