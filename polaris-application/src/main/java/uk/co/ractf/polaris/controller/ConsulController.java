package uk.co.ractf.polaris.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.kv.Operation;
import com.orbitz.consul.model.kv.Verb;
import com.orbitz.consul.model.session.ImmutableSession;
import com.orbitz.consul.model.session.Session;
import io.dropwizard.lifecycle.Managed;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.consul.ConsulPath;
import uk.co.ractf.polaris.controller.service.ControllerServices;
import uk.co.ractf.polaris.node.Node;
import uk.co.ractf.polaris.controller.instanceallocation.EphemeralInstanceAllocator;
import uk.co.ractf.polaris.controller.instanceallocation.InstanceAllocator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ConsulController implements Controller, Managed {

    private static final Logger log = LoggerFactory.getLogger(ConsulController.class);

    private final ControllerConfiguration config;
    private final Consul consul;
    private final Map<String, Node> hosts = new ConcurrentHashMap<>();
    private final InstanceAllocator instanceAllocator;
    private final Set<Service> services;
    private final Session session;
    private final String sessionId;

    @Inject
    public ConsulController(final ControllerConfiguration config,
                            final Consul consul,
                            @ControllerServices final Set<Service> services) {
        this.config = config;
        this.consul = consul;
        this.instanceAllocator = new EphemeralInstanceAllocator(this);
        this.services = services;
        this.session = ImmutableSession.builder().name("polaris-controller").build();
        this.sessionId = consul.sessionClient().createSession(session).getId();
    }

    @Override
    public void start() {
        for (final Service service : services) {
            service.startAsync();
        }
    }

    @Override
    public void stop() {
        for (final Service service : services) {
            service.stopAsync();
        }
    }

    @Override
    public void addHost(final Node node) {
        if (hosts.containsKey(node.getId())) {
            return;
        }
        hosts.put(node.getId(), node);
    }

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

    @Override
    public void createChallenge(final Challenge challenge) {
        Preconditions.checkArgument(!challenge.getId().isBlank(), "Challenge id cannot be blank.");
        if (getChallenge(challenge.getId()) != null) {
            throw new IllegalArgumentException("Challenge with id " + challenge.getId() + " already exists.");
        }
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
    public void createDeployment(final Deployment deployment) {
        Preconditions.checkArgument(!deployment.getId().isBlank(), "Deployment id cannot be blank.");
        if (getDeployment(deployment.getId()) != null) {
            throw new IllegalArgumentException("Challenge with id " + deployment.getId() + " already exists.");
        }
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.deployment(deployment.getId()))
                        .value(deployment.toJsonString())
                        .build());
    }

    @Override
    public void updateDeployment(final Deployment deployment) {
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

    @Override
    public Challenge getChallengeFromDeployment(final String deploymentId) {
        final Deployment deployment = getDeployment(deploymentId);
        if (deployment == null) {
            return null;
        }
        return getChallenge(deployment.getChallenge());
    }

    @Override
    public Challenge getChallengeFromDeployment(final Deployment deployment) {
        return getChallenge(deployment.getChallenge());
    }

    @Override
    public Map<String, Node> getHosts() {
        return Collections.unmodifiableMap(hosts);
    }

    @Override
    public Node getHost(final String id) {
        return hosts.get(id);
    }

    @Override
    public @NotNull List<Deployment> getDeploymentsOfChallenge(final String challenge) {
        final List<Deployment> deployments = new ArrayList<>();
        for (final Map.Entry<String, Deployment> entry : getDeployments().entrySet()) {
            final Deployment deployment = entry.getValue();
            if (deployment.getChallenge().equals(challenge)) {
                deployments.add(deployment);
            }
        }
        return deployments;
    }

    @Override
    public @NotNull List<Instance> getInstancesForDeployment(final String deployment) {
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

    @Override
    public InstanceAllocator getInstanceAllocator() {
        return instanceAllocator;
    }

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

}