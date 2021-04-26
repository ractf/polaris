package uk.co.ractf.polaris.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.kv.Operation;
import com.orbitz.consul.model.kv.Verb;
import com.orbitz.consul.model.session.ImmutableSession;
import com.orbitz.consul.model.session.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.util.ConsulPath;

import java.util.*;

@Singleton
public class ConsulState implements ClusterState {

    private static final Logger log = LoggerFactory.getLogger(ConsulState.class);

    private final Consul consul;
    private final String sessionId;

    @Inject
    public ConsulState(final Consul consul) {
        this.consul = consul;
        //TODO: this is probably terrible for debugging
        final Session session = ImmutableSession.builder().name(UUID.randomUUID().toString()).build();
        this.sessionId = consul.sessionClient().createSession(session).getId();
    }

    @NotNull
    @Override
    public Map<String, Challenge> getChallenges() {
        final Map<String, Challenge> challengeMap = new HashMap<>();
        final List<String> challengePaths = consul.keyValueClient().getKeys(ConsulPath.challenges());

        for (final String challengePath : challengePaths) {
            final Optional<String> challengeData = consul.keyValueClient().getValueAsString(challengePath);
            if (challengeData.isPresent()) {
                try {
                    final Challenge challenge = Challenge.parse(challengeData.get(), Challenge.class);
                    if (!challenge.getId().isBlank()) {
                        challengeMap.put(challenge.getId(), challenge);
                    }
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing challenge " + challengePath, exception);
                }
            }
        }

        return challengeMap;
    }

    @Nullable
    @Override
    public Challenge getChallenge(final String id) {
        final Optional<String> challengeData = consul.keyValueClient().getValueAsString(ConsulPath.challenge(id));
        if (challengeData.isPresent()) {
            try {
                return Challenge.parse(challengeData.get(), Challenge.class);
            } catch (final JsonProcessingException exception) {
                log.error("Error deserializing challenge " + id, exception);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Challenge getChallengeFromDeployment(final String deploymentId) {
        return null;
    }

    @Override
    public void setChallenge(final Challenge challenge) {
        Preconditions.checkArgument(!challenge.getId().isBlank(), "Challenge id cannot be blank.");
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.challenge(challenge.getId()))
                        .value(challenge.toJsonString())
                        .build());
    }

    @Override
    public void deleteChallenge(final String id) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.DELETE)
                        .key(ConsulPath.challenge(id))
                        .build());
    }

    @NotNull
    @Override
    public Map<String, Deployment> getDeployments() {
        final Map<String, Deployment> deploymentMap = new HashMap<>();
        final List<String> deploymentPaths = consul.keyValueClient().getKeys(ConsulPath.deployments());

        for (final String deploymentPath : deploymentPaths) {
            final Optional<String> deploymentData = consul.keyValueClient().getValueAsString(deploymentPath);
            if (deploymentData.isPresent()) {
                try {
                    final Deployment deployment = Deployment.parse(deploymentData.get(), Deployment.class);
                    if (!deployment.getId().isBlank()) {
                        deploymentMap.put(deployment.getId(), deployment);
                    }
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing deployment " + deploymentPath, exception);
                }
            }
        }

        return deploymentMap;
    }

    @Nullable
    @Override
    public Deployment getDeployment(final String id) {
        final Optional<String> deploymentData = consul.keyValueClient().getValueAsString(ConsulPath.deployment(id));
        if (deploymentData.isPresent()) {
            try {
                return Deployment.parse(deploymentData.get(), Deployment.class);
            } catch (final JsonProcessingException exception) {
                log.error("Error deserializing deployment " + id, exception);
            }
        }
        return null;
    }

    @Override
    public void setDeployment(final Deployment deployment) {
        Preconditions.checkArgument(!deployment.getId().isBlank(), "Deployment id cannot be blank.");
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.deployment(deployment.getId()))
                        .value(deployment.toJsonString())
                        .build());
    }

    @Override
    public void deleteDeployment(final String id) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.DELETE)
                        .key(ConsulPath.deployment(id))
                        .build());
    }

    @NotNull
    @Override
    public Map<String, NodeInfo> getNodes() {
        final Map<String, NodeInfo> nodeMap = new HashMap<>();
        final List<String> nodePaths = consul.keyValueClient().getKeys(ConsulPath.nodes());

        for (final String nodePath : nodePaths) {
            final Optional<String> nodeData = consul.keyValueClient().getValueAsString(nodePath);
            if (nodeData.isPresent()) {
                try {
                    final NodeInfo node = NodeInfo.parse(nodeData.get(), NodeInfo.class);
                    if (!node.getId().isBlank()) {
                        nodeMap.put(node.getId(), node);
                    }
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing node " + nodePath, exception);
                }
            }
        }

        return nodeMap;
    }

    @Nullable
    @Override
    public NodeInfo getNode(final String id) {
        final Optional<String> nodeData = consul.keyValueClient().getValueAsString(ConsulPath.node(id));
        if (nodeData.isPresent()) {
            try {
                return NodeInfo.parse(nodeData.get(), NodeInfo.class);
            } catch (final JsonProcessingException exception) {
                log.error("Error deserializing node " + id, exception);
            }
        }
        return null;
    }

    @NotNull
    @Override
    public List<Deployment> getDeploymentsOfChallenge(final String challenge) {
        final List<Deployment> deployments = new ArrayList<>();
        for (final Map.Entry<String, Deployment> entry : getDeployments().entrySet()) {
            final Deployment deployment = entry.getValue();
            if (deployment.getChallenge().equals(challenge)) {
                deployments.add(deployment);
            }
        }
        return deployments;
    }

    @NotNull
    @Override
    public List<Instance> getInstancesForDeployment(final String deployment) {
        final List<Instance> instances = new ArrayList<>();
        final List<String> instancePaths = consul.keyValueClient().getKeys(ConsulPath.instances());
        for (final String instancePath : instancePaths) {
            final Optional<String> instanceData = consul.keyValueClient().getValueAsString(instancePath);
            if (instanceData.isPresent()) {
                try {
                    final Instance instance = Instance.parse(instanceData.get(), Instance.class);
                    if (instance.getDeploymentId().equals(deployment)) {
                        instances.add(instance);
                    }
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing instance " + instancePath, exception);
                }
            }
        }
        return instances;
    }

    @Nullable
    @Override
    public Instance getInstance(final String id) {
        final Optional<String> instanceData = consul.keyValueClient().getValueAsString(ConsulPath.instance(id));
        if (instanceData.isPresent()) {
            try {
                return Instance.parse(instanceData.get(), Instance.class);
            } catch (final JsonProcessingException exception) {
                log.error("Error deserializing instance " + id, exception);
            }
        }
        return null;
    }

    @Override
    public void registerInstance(final Deployment deployment, final Instance instance) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.instance(instance.getId()))
                        .value(instance.toJsonString())
                        .build());
    }

    @Override
    public void unregisterInstance(final Deployment deployment, final Instance instance) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.DELETE)
                        .key(ConsulPath.instance(instance.getId()))
                        .build());
    }

    @Override
    public boolean lockDeployment(final Deployment deployment) {
        try {
            return consul.keyValueClient().acquireLock(ConsulPath.deploymentLock(deployment.getId()), sessionId);
        } catch (final Exception e) {
            log.error("Error obtaining lock", e);
            return false;
        }
    }

    @Override
    public boolean unlockDeployment(final Deployment deployment) {
        try {
            return consul.keyValueClient().releaseLock(ConsulPath.deploymentLock(deployment.getId()), sessionId);
        } catch (final Exception e) {
            log.error("Error releasing lock", e);
            return false;
        }
    }

    @NotNull
    @Override
    public Map<String, Instance> getInstancesOnNode(final String node) {
        final Map<String, Instance> instances = new HashMap<>();
        final List<String> instancePaths = consul.keyValueClient().getKeys(ConsulPath.instances());
        for (final String instancePath : instancePaths) {
            final Optional<String> instanceData = consul.keyValueClient().getValueAsString(instancePath);
            if (instanceData.isPresent()) {
                try {
                    final Instance instance = Instance.parse(instanceData.get(), Instance.class);
                    if (instance.getNodeId().equals(node)) {
                        instances.put(instance.getId(), instance);
                    }
                } catch (final JsonProcessingException exception) {
                    log.error("Error deserializing instance " + instancePath, exception);
                }
            }
        }
        return instances;
    }
}
