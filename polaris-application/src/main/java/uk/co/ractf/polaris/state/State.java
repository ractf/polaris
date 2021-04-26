package uk.co.ractf.polaris.state;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.node.Node;

import java.util.List;
import java.util.Map;

public interface State {

    /**
     * Gets a {@link Map} of challenge id to {@link Challenge}
     *
     * @return challenge map
     */
    @NotNull
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
     * Resolves a {@link Deployment} id into a challenge
     *
     * @param deploymentId the deployment id
     * @return the challenge that deployment deploys
     */
    @Nullable
    Challenge getChallengeFromDeployment(String deploymentId);

    /**
     * Sets the data of a {@link Challenge}
     *
     * @param challenge the challenge object
     */
    void setChallenge(final Challenge challenge);

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
    @NotNull
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
    void setDeployment(final Deployment deployment);

    /**
     * Gets a {@link Map} of host id to {@link Node}
     *
     * @return map of hosts
     */
    @NotNull
    Map<String, NodeInfo> getNodes();

    /**
     * Gets a {@link NodeInfo} by id
     *
     * @param id host id
     * @return the host
     */
    @Nullable
    NodeInfo getNode(final String id);

    /**
     * Get all {@link Deployment}s of a {@link Challenge}
     *
     * @param challenge challenge id
     * @return all deployments of that challenge
     */
    @NotNull
    List<Deployment> getDeploymentsOfChallenge(final String challenge);

    /**
     * Get all {@link Instance}s that are part of a {@link Deployment}
     *
     * @param deployment the deployment id
     * @return the instance list
     */
    @NotNull
    List<Instance> getInstancesForDeployment(final String deployment);

    /**
     * Gets an {@link Instance} by id
     *
     * @param id the instance id
     * @return the instance
     */
    @Nullable
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
