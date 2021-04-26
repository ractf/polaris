package uk.co.ractf.polaris.controller;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.node.Node;
import uk.co.ractf.polaris.controller.instanceallocation.InstanceAllocator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * The interface that defines what information the Polaris controller needs to receive and how the rest of the application
 * should interact with it.
 */
public interface Controller {

    /**
     * Registers a {@link Node} with the controller
     *
     * @param node the host to add
     */
    void addHost(final Node node);

    /**
     * Gets a {@link Map} of challenge id to {@link Challenge}
     *
     * @return challenge map
     */
    Map<String, Challenge> getChallenges();

    /**
     * Gets a {@link Challenge} with a specific id
     *
     * @param id the challenge id
     * @return the challenge
     */
    @Nullable
    Challenge getChallenge(final String id);

    /**
     * Submits a {@link Challenge} that can later be deployed
     *
     * @param challenge the challenge object
     */
    void createChallenge(final Challenge challenge);

    /**
     * Remove a {@link Challenge}
     *
     * @param id the id of the challenge
     */
    void deleteChallenge(final String id);

    /**
     * Gets a {@link Map} of deployment id to {@link Deployment}
     *
     * @return deployment map
     */
    Map<String, Deployment> getDeployments();

    /**
     * Gets a {@link Deployment} by id
     *
     * @param id the id of the deployment
     * @return the deployment
     */
    @Nullable
    Deployment getDeployment(final String id);

    /**
     * Submits a {@link Deployment} to be scheduled
     *
     * @param deployment the deployment
     */
    void createDeployment(final Deployment deployment);

    /**
     * Does an in place update of an existing {@link Deployment}
     *
     * @param deployment the new deployment config
     */
    void updateDeployment(final Deployment deployment);

    /**
     * Deletes a {@link Deployment } from the controller, which will eventually cause all the {@link Instance}s of that
     * deployment to be descheduled
     *
     * @param id the deployment id to delete
     */
    void deleteDeployment(final String id);

    /**
     * Resolves a {@link Deployment} id into a challenge
     *
     * @param deployment the deployment id
     * @return the challenge that deployment deploys
     */
    @Nullable
    Challenge getChallengeFromDeployment(final String deployment);

    /**
     * Resolves a {@link Deployment}'s challenge id into a challenge
     *
     * @param deployment the deployment
     * @return that deployment's challenge
     */
    @Nullable
    Challenge getChallengeFromDeployment(final Deployment deployment);

    /**
     * Gets a {@link Map} of host id to {@link Node}
     *
     * @return map of hosts
     */
    Map<String, Node> getHosts();

    /**
     * Gets a {@link Node} by id
     *
     * @param id host id
     * @return the host
     */
    @Nullable
    Node getHost(final String id);

    /**
     * Get all {@link Deployment}s of a {@link Challenge}
     *
     * @param challenge challenge id
     * @return all deployments of that challenge
     */
    @Nonnull
    List<Deployment> getDeploymentsOfChallenge(final String challenge);

    /**
     * Get all {@link Instance}s that are part of a {@link Deployment}
     *
     * @param deployment the deployment id
     * @return the instance list
     */
    @Nonnull
    List<Instance> getInstancesForDeployment(final String deployment);

    /**
     * Returns the {@link InstanceAllocator} that should be used to decide which {@link Instance}s are allocated to which users
     *
     * @return the instance allocator
     */
    InstanceAllocator getInstanceAllocator();

    /**
     * Gets an {@link Instance} by id
     *
     * @param id the instance id
     * @return the instance
     */
    Instance getInstance(final String id);

    /**
     * Register a newly scheduled {@link Instance} to a {@link Deployment}
     *
     * @param deployment the deployment of the instance
     * @param instance   the instance
     */
    void registerInstance(final Deployment deployment, final Instance instance);

    /**
     * Unregisters an {@link Instance} from a given {@link Deployment}, probably because its been descheduled
     *
     * @param deployment the deployment of the instance
     * @param instance   the instance
     */
    void unregisterInstance(final Deployment deployment, final Instance instance);

    /**
     * Locks a deployment's set of instances so modifications can be made to it
     *
     * @param deployment the deployment
     * @return if the lock was successfully acquired
     */
    boolean lockDeployment(final Deployment deployment);

    /**
     * Frees the lock on a deployment's set of instances
     *
     * @param deployment the deployment
     * @return if the lock was successfully released
     */
    @CanIgnoreReturnValue
    boolean unlockDeployment(final Deployment deployment);

}
