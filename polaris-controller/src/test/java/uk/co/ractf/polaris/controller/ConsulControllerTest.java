package uk.co.ractf.polaris.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.Service;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.SessionClient;
import com.orbitz.consul.model.session.ImmutableSessionCreatedResponse;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.ractf.polaris.PolarisConfiguration;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.consul.ConsulPath;
import uk.co.ractf.polaris.host.Node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class ConsulControllerTest {

    private static final Consul consul = mock(Consul.class);
    private static final KeyValueClient keyValueClient = mock(KeyValueClient.class);

    private static PolarisConfiguration config;
    private static ConsulController controller;

    @BeforeEach
    public void setup() {
        config = new PolarisConfiguration();
        when(consul.keyValueClient()).thenReturn(keyValueClient);
        final SessionClient sessionClient = mock(SessionClient.class);
        when(consul.sessionClient()).thenReturn(sessionClient);
        when(sessionClient.createSession(any())).thenReturn(ImmutableSessionCreatedResponse.builder().id("test").build());
        when(keyValueClient.getKeys(ConsulPath.challenges())).thenReturn(Collections.singletonList(ConsulPath.challenge("test")));
        controller = new ConsulController(config, consul, Collections.emptySet());
    }

    @Test
    public void testGetChallenges() throws JsonProcessingException {
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.of(fixture("fixtures/valid_challenge.json")));
        final Challenge challenge = Challenge.parse(fixture("fixtures/valid_challenge.json"), Challenge.class);
        final Map<String, Challenge> expected = new HashMap<>();
        expected.put("test", challenge);
        assertThat(controller.getChallenges()).containsAllEntriesOf(expected);
    }

    @Test
    public void testGetChallengesExcludesBlank() {
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.of(fixture("fixtures/blank_id_challenge.json")));
        assertThat(controller.getChallenges()).isEmpty();
    }

    @Test
    public void testGetChallengesInvalidJsonDoesntThrowException() {
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.of(fixture("fixtures/invalid_json_challenge.json")));
        assertDoesNotThrow(controller::getChallenges);
    }

    @Test
    public void testGetChallengesMissingData() {
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.empty());
        assertThat(controller.getChallenges()).isEmpty();
    }

    @Test
    public void testGetChallenge() throws JsonProcessingException {
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.of(fixture("fixtures/valid_challenge.json")));
        final Challenge challenge = Challenge.parse(fixture("fixtures/valid_challenge.json"), Challenge.class);
        assertThat(controller.getChallenge("test")).isEqualTo(challenge);
    }

    @Test
    public void testGetChallengeInvalidId() {
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.empty());
        assertThat(controller.getChallenge("test")).isNull();
    }

    @Test
    public void testGetChallengeInvalidJsonDoesntThrowException() {
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.of(fixture("fixtures/invalid_json_challenge.json")));
        assertDoesNotThrow(() -> controller.getChallenge("test"));
    }

    @Test
    public void testCreateChallenge() throws JsonProcessingException {
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.empty());
        when(keyValueClient.performTransaction(any())).thenReturn(null);
        controller.createChallenge(Challenge.parse(fixture("fixtures/valid_challenge.json"), Challenge.class));
        verify(keyValueClient, times(1)).performTransaction(any());
    }

    @Test
    public void testCreateChallengeRejectsBlankId() {
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.empty());
        when(keyValueClient.performTransaction(any())).thenReturn(null);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> controller.createChallenge(Challenge.parse(fixture("fixtures/blank_id_challenge.json"), Challenge.class)));
    }

    @Test
    public void testCreateChallengeRejectsDuplicate() {
        when(keyValueClient.getValueAsString(ConsulPath.challenge("test"))).thenReturn(Optional.of(fixture("fixtures/valid_challenge.json")));
        when(keyValueClient.performTransaction(any())).thenReturn(null);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> controller.createChallenge(Challenge.parse(fixture("fixtures/valid_challenge.json"), Challenge.class)));
    }

    @Test
    public void testServicesStartAsync() {
        final Service service = mock(Service.class);
        controller = new ConsulController(config, consul, Collections.singleton(service));
        controller.start();
        verify(service, times(1)).startAsync();
    }

    @Test
    public void testServicesStopAsync() {
        final Service service = mock(Service.class);
        final ConsulController controller = new ConsulController(config, consul, Collections.singleton(service));
        controller.stop();
        verify(service, times(1)).stopAsync();
    }

    @Test
    public void testHostAdding() {
        final Node node = mock(Node.class);
        when(node.getId()).thenReturn("testHost");
        controller.addHost(node);
        assertThat(controller.getHost("testHost")).isEqualTo(node);
    }

    @Test
    public void testHostAddingDuplicate() {
        final Node node = mock(Node.class);
        when(node.getId()).thenReturn("testHost");
        final Node node2 = mock(Node.class);
        when(node2.getId()).thenReturn("testHost");
        controller.addHost(node);
        controller.addHost(node2);
        assertThat(controller.getHost("testHost")).isEqualTo(node);
    }

    @Test
    public void testGetHosts() {
        final Map<String, Node> expected = new HashMap<>();
        final Node node = mock(Node.class);
        when(node.getId()).thenReturn("testHost");
        expected.put("testHost", node);
        final Node node2 = mock(Node.class);
        when(node2.getId()).thenReturn("testHost2");
        expected.put("testHost2", node2);
        controller.addHost(node);
        controller.addHost(node2);
        assertThat(controller.getHosts()).containsAllEntriesOf(expected);
    }

}
