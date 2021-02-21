package uk.co.ractf.polaris.controller.task;

import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.host.Host;
import uk.co.ractf.polaris.replication.ReplicationController;
import uk.co.ractf.polaris.scheduler.Scheduler;

import java.util.List;

public class DeploymentScaleReconciler implements Runnable {

    private final Controller controller;
    private final Scheduler scheduler;
    private final String deploymentID;

    public DeploymentScaleReconciler(final String deploymentID, final Controller controller, final Scheduler scheduler) {
        this.controller = controller;
        this.scheduler = scheduler;
        this.deploymentID = deploymentID;
    }

    @Override
    public void run() {
        final Deployment deployment = controller.getDeployment(deploymentID);
        controller.lockDeployment(deployment);
        final List<Instance> instances = controller.getInstancesForDeployment(deployment.getID());
        final Challenge challenge = controller.getChallengeFromDeployment(deployment);

        final int scaleAmount = ReplicationController.create(deployment.getReplication()).getScaleAmount(instances, controller);
        if (scaleAmount > 0) {
            for (int i = 0; i < scaleAmount; i++) {
                final Host host = scheduler.scheduleChallenge(challenge, controller.getHosts().values());
                final Instance instance = host.createInstance(challenge, deployment);
                controller.registerInstance(deployment, instance);
            }
        } else {
            for (int i = 0; i > scaleAmount; i--) {
                final Instance instance = scheduler.descheduleInstance(challenge, controller.getHosts().values(), instances);
                controller.unregisterInstance(deployment, instance);
            }
        }
        controller.unlockDeployment(deployment);
    }
}
