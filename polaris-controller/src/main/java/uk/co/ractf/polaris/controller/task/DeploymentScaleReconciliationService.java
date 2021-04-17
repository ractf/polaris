package uk.co.ractf.polaris.controller.task;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.PolarisConfiguration;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.host.Host;
import uk.co.ractf.polaris.replication.ReplicationController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class DeploymentScaleReconciliationService extends AbstractScheduledService {

    private static final Logger log = LoggerFactory.getLogger(DeploymentScaleReconciliationService.class);

    private final Controller controller;
    private final uk.co.ractf.polaris.scheduler.Scheduler scheduler;
    private final PolarisConfiguration polarisConfiguration;

    @Inject
    public DeploymentScaleReconciliationService(final Controller controller,
                                                final uk.co.ractf.polaris.scheduler.Scheduler scheduler,
                                                final PolarisConfiguration polarisConfiguration) {
        this.controller = controller;
        this.scheduler = scheduler;
        this.polarisConfiguration = polarisConfiguration;
    }

    @Override
    protected void runOneIteration() {
        try {
            log.trace("Starting deployment reconciliation tick {}", controller.getDeployments().size());
            for (final Deployment deployment : controller.getDeployments().values()) {
                log.debug("Attempting to reconcile deployment {}", deployment.getID());
                if (!controller.lockDeployment(deployment)) {
                    log.debug("Could not obtain lock for {}", deployment.getID());
                    continue;
                }
                final List<Instance> instances = controller.getInstancesForDeployment(deployment.getID());
                final Challenge challenge = controller.getChallengeFromDeployment(deployment);

                final int scaleAmount = ReplicationController.create(deployment.getReplication()).getScaleAmount(instances, controller);
                log.debug("Replication: static {} {}", ((StaticReplication) deployment.getReplication()).getAmount(), instances.size());
                log.debug("Scaling required for {}: {}", deployment.getID(), scaleAmount);
                if (scaleAmount > 0) {
                    log.info("Scheduling instances: {} of {}", scaleAmount, deployment.getID());
                    for (int i = 0; i < scaleAmount; i++) {
                        final Host host = scheduler.scheduleChallenge(challenge, controller.getHosts().values());
                        final Instance instance = host.createInstance(challenge, deployment);
                        log.info("Scheduled instance of {} onto {}", instance.getID(), host.getID());
                        controller.registerInstance(deployment, instance);
                        log.info("Instance of {} successfully registered on {}", instance.getID(), host.getID());
                    }
                } else {
                    for (int i = 0; i > scaleAmount; i--) {
                        final Instance instance = scheduler.descheduleInstance(challenge, controller.getHosts().values(), instances);
                        controller.unregisterInstance(deployment, instance);
                    }
                }
                controller.unlockDeployment(deployment);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(polarisConfiguration.getReconciliationTickFrequency(), polarisConfiguration.getReconciliationTickFrequency(), TimeUnit.MILLISECONDS);
    }
}
