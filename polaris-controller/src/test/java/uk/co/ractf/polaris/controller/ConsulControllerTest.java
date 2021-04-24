package uk.co.ractf.polaris.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.SessionClient;
import com.orbitz.consul.model.session.ImmutableSessionCreatedResponse;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import uk.co.ractf.polaris.PolarisConfiguration;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.consul.ConsulPath;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class ConsulControllerTest {

    private PolarisConfiguration config;
    private final Consul consul = mock(Consul.class);
    private final KeyValueClient keyValueClient = mock(KeyValueClient.class);

    @BeforeEach
    public void setup() {
        config = new PolarisConfiguration();
        when(consul.keyValueClient()).thenReturn(keyValueClient);
        final SessionClient sessionClient = mock(SessionClient.class);
        when(consul.sessionClient()).thenReturn(sessionClient);
        when(sessionClient.createSession(any())).thenReturn(ImmutableSessionCreatedResponse.builder().id("test").build());
        when(keyValueClient.getKeys(ConsulPath.challenges())).thenReturn(Collections.singletonList(ConsulPath.challenge("test")));
    }

    @Test
    public void testGetChallenges() throws JsonProcessingException {
        final ConsulController controller = new ConsulController(config, consul, Collections.emptySet());
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.of(fixture("fixtures/valid_challenge.json")));
        final Challenge challenge = Challenge.parse(fixture("fixtures/valid_challenge.json"), Challenge.class);
        final Map<String, Challenge> expected = new HashMap<>();
        expected.put("test", challenge);
        assertThat(controller.getChallenges()).containsAllEntriesOf(expected);
    }

    @Test
    public void testGetChallengesExcludesBlank() {
        final ConsulController controller = new ConsulController(config, consul, Collections.emptySet());
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.of(fixture("fixtures/blank_id_challenge.json")));
        assertThat(controller.getChallenges()).isEmpty();
    }

    @Test
    public void testGetChallengesInvalidJsonDoesntThrowException() {
        final ConsulController controller = new ConsulController(config, consul, Collections.emptySet());
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.of(fixture("fixtures/invalid_json_challenge.json")));
        assertDoesNotThrow(controller::getChallenges);
    }

}
