package uk.co.ractf.polaris.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.PolarisConfiguration;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.host.Host;
import uk.co.ractf.polaris.instanceallocation.EphemeralInstanceAllocator;
import uk.co.ractf.polaris.instanceallocation.InstanceAllocator;
import uk.co.ractf.polaris.replication.ReplicationController;
import uk.co.ractf.polaris.scheduler.RoundRobinScheduler;
import uk.co.ractf.polaris.scheduler.Scheduler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EphemeralController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(EphemeralController.class);

    private final Map<String, Challenge> challenges = new ConcurrentHashMap<>();
    private final Map<String, Deployment> deployments = new ConcurrentHashMap<>();
    private final Map<String, SemaphoreInstanceList> deploymentInstances = new ConcurrentHashMap<>();
    private final Map<String, Instance> instances = new ConcurrentHashMap<>();
    private final Map<String, Host> hosts = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService;
    private final ExecutorService executorService;
    private final Scheduler scheduler = new RoundRobinScheduler();
    private final InstanceAllocator instanceAllocator = new EphemeralInstanceAllocator(this);

    public EphemeralController(final PolarisConfiguration config,
                               final ScheduledExecutorService scheduledExecutorService,
                               final ExecutorService executorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.executorService = executorService;
        this.scheduledExecutorService.scheduleAtFixedRate(this::reconciliationTick,
                config.getReconciliationTickFrequency(),
                config.getReconciliationTickFrequency(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void addHost(final Host host) {
        hosts.put(host.getID(), host);
    }

    @Override
    public Map<String, Challenge> getChallenges() {
        return Collections.unmodifiableMap(challenges);
    }

    @Override
    public Challenge getChallenge(final String id) {
        return challenges.get(id);
    }

    @Override
    public void submitChallenge(final Challenge challenge) {
        challenges.put(challenge.getID(), challenge);
    }

    @Override
    public void deleteChallenge(final String id) {
        challenges.remove(id);
    }

    @Override
    public Map<String, Deployment> getDeployments() {
        return Collections.unmodifiableMap(deployments);
    }

    @Override
    public Deployment getDeployment(final String id) {
        return deployments.get(id);
    }

    @Override
    public void createDeployment(final Deployment deployment) {
        deployments.put(deployment.getID(), deployment);
    }

    @Override
    public void updateDeployment(final Deployment deployment) {
        deployments.put(deployment.getID(), deployment);
    }

    @Override
    public void deleteDeployment(final String id) {
        deployments.remove(id);
    }

    @Override
    public Challenge getChallengeFromDeployment(final String deployment) {
        final Deployment deployment1 = deployments.get(deployment);
        return getChallengeFromDeployment(deployment1);
    }

    @Override
    public Challenge getChallengeFromDeployment(final Deployment deployment) {
        return challenges.get(deployment.getChallenge());
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
        for (final Deployment deployment : this.deployments.values()) {
            if (deployment.getChallenge().equals(challenge)) {
                deployments.add(deployment);
            }
        }

        return deployments;
    }

    @Override
    public List<Instance> getInstancesForDeployment(final String deployment) {
        return deploymentInstances.get(deployment).getReadOnlyInstances();
    }

    @Override
    public InstanceAllocator getInstanceAllocator() {
        return instanceAllocator;
    }

    @Override
    public Instance getInstance(final String id) {
        return instances.get(id);
    }

    @Override
    public void registerInstance(final Deployment deployment, final Instance instance) {
        instances.put(instance.getID(), instance);
        deploymentInstances.get(deployment.getID()).getInstances().add(instance);
    }

    @Override
    public void unregisterInstance(final Deployment deployment, final Instance instance) {
        instances.remove(instance.getID());
    }

    @Override
    public boolean lockDeployment(final Deployment deployment) {
        return false;
    }

    @Override
    public boolean unlockDeployment(final Deployment deployment) {
        return false;
    }

    private void reconciliationTick() {
        log.debug("Running controller reconciliation tick");
        /*for (final String deploymentID : deployments.keySet()) {
            executorService.submit(() -> {
                final Deployment deployment = deployments.get(deploymentID);
                final Challenge challenge = challenges.get(deployment.getChallenge());
                final SemaphoreInstanceList semaphoreInstanceList = deploymentInstances.computeIfAbsent(deploymentID, x -> new SemaphoreInstanceList());
                if (semaphoreInstanceList.isBusy()) {
                    return;
                }
                final List<Instance> deploymentInstances = semaphoreInstanceList.getInstances();
                final int scaleAmount = ReplicationController.create(deployment.getReplication()).getScaleAmount(deploymentInstances, this);
                if (scaleAmount > 0) {
                    for (int i = 0; i < scaleAmount; i++) {
                        final Host host = scheduler.scheduleChallenge(challenge, hosts.values());
                        final Instance instance = host.createInstance(challenge, deployment);
                        deploymentInstances.add(instance);
                        instances.put(instance.getID(), instance);
                        log.debug("Scheduled instance {}(challenge: {}, deployment: {}) onto {}", instance.getID(), challenge.getID(), deployment.getID(), host.getID());
                    }
                }
                semaphoreInstanceList.free();
            });
        }*/

        for (final String deploymentID : deploymentInstances.keySet()) {
            if (!deployments.containsKey(deploymentID)) {
                executorService.submit(() -> {
                    final SemaphoreInstanceList semaphoreInstanceList = deploymentInstances.computeIfAbsent(deploymentID, x -> new SemaphoreInstanceList());
                    final List<Instance> instanceList = semaphoreInstanceList.getInstances();
                    for (final Instance instance : instanceList) {
                        hosts.get(instance.getHostID()).removeInstance(instance);
                        instances.remove(instance.getID());
                    }
                    semaphoreInstanceList.free();
                    deploymentInstances.remove(deploymentID);
                });
            }
        }
    }

}
