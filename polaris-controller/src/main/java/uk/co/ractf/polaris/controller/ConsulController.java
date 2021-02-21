package uk.co.ractf.polaris.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.kv.Operation;
import com.orbitz.consul.model.kv.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.PolarisConfiguration;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.consul.ConsulPath;
import uk.co.ractf.polaris.host.Host;
import uk.co.ractf.polaris.instanceallocation.EphemeralInstanceAllocator;
import uk.co.ractf.polaris.instanceallocation.InstanceAllocator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class ConsulController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(ConsulController.class);

    private final PolarisConfiguration config;
    private final Consul consul;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ExecutorService executorService;
    private final Map<String, Host> hosts = new ConcurrentHashMap<>();
    private final InstanceAllocator instanceAllocator;

    public ConsulController(final PolarisConfiguration config,
                            final Consul consul,
                            final ScheduledExecutorService scheduledExecutorService,
                            final ExecutorService executorService) {
        this.config = config;
        this.consul = consul;
        this.scheduledExecutorService = scheduledExecutorService;
        this.executorService = executorService;
        this.instanceAllocator = new EphemeralInstanceAllocator(this);
    }

    @Override
    public void addHost(final Host host) {
        hosts.put(host.getID(), host);
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
                    challengeMap.put(challenge.getID(), challenge);
                } catch (JsonProcessingException e) {
                    log.error("Error deserializing challenge " + challengePath, e);
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
            } catch (JsonProcessingException e) {
                log.error("Error deserializing challenge " + id, e);
            }
        }
        return null;
    }

    @Override
    public void submitChallenge(final Challenge challenge) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.challenge(challenge.getID()))
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
                    deploymentMap.put(deployment.getID(), deployment);
                } catch (JsonProcessingException e) {
                    log.error("Error deserializing deployment " + deploymentPath, e);
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
            } catch (JsonProcessingException e) {
                log.error("Error deserializing deployment " + id, e);
            }
        }
        return null;
    }

    @Override
    public void createDeployment(final Deployment deployment) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.deployment(deployment.getID()))
                        .value(deployment.toJsonString())
                        .build());
    }

    @Override
    public void updateDeployment(final Deployment deployment) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.deployment(deployment.getID()))
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
    public Challenge getChallengeFromDeployment(final String deployment) {
        return getChallenge(getDeployment(deployment).getChallenge());
    }

    @Override
    public Challenge getChallengeFromDeployment(final Deployment deployment) {
        return getChallenge(deployment.getChallenge());
    }

    @Override
    public Map<String, Host> getHosts() {
        return Collections.unmodifiableMap(hosts);
    }

    @Override
    public Host getHost(final String id) {
        return hosts.get(id);
    }

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

    @Override
    public List<Instance> getInstancesForDeployment(final String deployment) {
        final List<Instance> instances = new ArrayList<>();
        final List<String> instancePaths = consul.keyValueClient().getKeys(ConsulPath.instances());
        for (final String instancePath : instancePaths) {
            final Optional<String> instanceData = consul.keyValueClient().getValueAsString(instancePath);
            if (instanceData.isPresent()) {
                try {
                    final Instance instance = Instance.parse(instanceData.get(), Instance.class);
                    instances.add(instance);
                } catch (JsonProcessingException e) {
                    log.error("Error deserializing instance " + instancePath, e);
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
            } catch (JsonProcessingException e) {
                log.error("Error deserializing instance " + id, e);
            }
        }
        return null;
    }

    @Override
    public void registerInstance(final Deployment deployment, final Instance instance) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.instance(instance.getID()))
                        .value(instance.toJsonString())
                        .build());
    }

    @Override
    public void unregisterInstance(final Deployment deployment, final Instance instance) {
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.DELETE)
                        .key(ConsulPath.instance(instance.getID()))
                        .build());
    }

    @Override
    public boolean lockDeployment(final Deployment deployment) {
        return consul.keyValueClient().acquireLock(ConsulPath.deployment(deployment.getID()), "polaris");
    }

    @Override
    public boolean unlockDeployment(final Deployment deployment) {
        return consul.keyValueClient().releaseLock(ConsulPath.deployment(deployment.getID()), "polaris");
    }

}
