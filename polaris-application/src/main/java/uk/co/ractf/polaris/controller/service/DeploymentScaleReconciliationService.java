package uk.co.ractf.polaris.controller.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.annotation.ExcludeFromGeneratedTestReport;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.controller.replication.ReplicationController;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
public class DeploymentScaleReconciliationService extends AbstractScheduledService {

    private static final Logger log = LoggerFactory.getLogger(DeploymentScaleReconciliationService.class);

    private final ClusterState clusterState;
    private final uk.co.ractf.polaris.controller.scheduler.Scheduler scheduler;
    private final ControllerConfiguration controllerConfiguration;

    @Inject
    public DeploymentScaleReconciliationService(final ClusterState controller,
                                                final uk.co.ractf.polaris.controller.scheduler.Scheduler scheduler,
                                                final ControllerConfiguration controllerConfiguration) {
        this.clusterState = controller;
        this.scheduler = scheduler;
        this.controllerConfiguration = controllerConfiguration;
    }

    @Override
    protected void runOneIteration() {
        try {
            log.trace("Starting deployment reconciliation tick {}", clusterState.getDeployments().size());
            for (final Deployment deployment : clusterState.getDeployments().values()) {
                log.debug("Attempting to reconcile deployment {}", deployment.getId());
                if (!clusterState.lockDeployment(deployment)) {
                    log.debug("Could not obtain lock for {}", deployment.getId());
                    continue;
                }
                final List<Instance> instances = clusterState.getInstancesForDeployment(deployment.getId());
                final Challenge challenge = clusterState.getChallengeFromDeployment(deployment.getId());

                final int scaleAmount = ReplicationController.create(deployment.getReplication()).getScaleAmount(instances, clusterState);
                log.debug("Replication: static {} {}", ((StaticReplication) deployment.getReplication()).getAmount(), instances.size());
                log.debug("Scaling required for {}: {}", deployment.getId(), scaleAmount);
                if (scaleAmount > 0) {
                    log.info("Scheduling instances: {} of {}", scaleAmount, deployment.getId());
                    for (int i = 0; i < scaleAmount; i++) {
                        final NodeInfo node = scheduler.scheduleChallenge(challenge, clusterState.getNodes().values());
                        log.info("Scheduled instance of {} onto {}", challenge.getId(), node.getId());
                        final Instance instance = new Instance(UUID.randomUUID().toString(), deployment.getId(), challenge.getId(),
                                node.getId(), new ArrayList<>(), new HashMap<>());
                        clusterState.registerInstance(deployment, instance);
                        log.info("Instance of {} successfully registered on {}", instance.getId(), node.getId());
                    }
                } else {
                    for (int i = 0; i > scaleAmount; i--) {
                        final Instance instance = scheduler.descheduleInstance(challenge, clusterState.getNodes().values(), instances);
                        clusterState.unregisterInstance(deployment, instance);
                    }
                }
                clusterState.unlockDeployment(deployment);
            }
        } catch (final Exception e) {
            log.error("Error reconciling deployments", e);
        }
    }

    @Override
    @ExcludeFromGeneratedTestReport
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(controllerConfiguration.getReconciliationTickFrequency(), controllerConfiguration.getReconciliationTickFrequency(), TimeUnit.MILLISECONDS);
    }
}
