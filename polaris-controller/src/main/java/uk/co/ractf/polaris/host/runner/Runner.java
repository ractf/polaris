package uk.co.ractf.polaris.host.runner;

import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.controller.Controller;

/**
 * Provides the interface to manage execution of a subclass of {@link Pod}
 *
 * @param <T> The type of pod this runner can run
 */
public interface Runner<T extends Pod> {

    /**
     * Starts a {@link Pod} with given {@link Instance} details
     *
     * @param pod      the pod
     * @param instance instance details
     */
    void startPod(final T pod, final Instance instance);

    /**
     * Stops a {@link Pod} with given {@link Instance} details
     *
     * @param pod      the pod
     * @param instance instance details
     */
    void stopPod(final T pod, final Instance instance);

    /**
     * Forcefully update a specific {@link Instance} of a {@link Pod}
     *
     * @param pod      the pod
     * @param instance the instance to update
     */
    void forceUpdatePod(final T pod, final Instance instance);

    /**
     * Restart a specific {@link Instance} of a {@link Pod}
     *
     * @param pod      the pod
     * @param instance the instance to restart
     */
    void restartPod(final T pod, final Instance instance);

    /**
     * Returns if the runner is currently able to start this {@link Pod}
     *
     * @param pod the pod
     * @return can it be started
     */
    boolean canStartPod(final T pod);

    /**
     * Returns if the runner has started this {@link Instance} of this {@link Pod}
     *
     * @param pod      the pod
     * @param instance the instance
     * @return if its started
     */
    boolean isPodStarted(final T pod, final Instance instance);

    /**
     * Prepares a {@link Pod} that currently cannot be started so it can be started in the future (e.g. {@link DockerRunner}
     * will pull the docker image)
     *
     * @param pod the pod to prepare
     */
    void preparePod(final T pod);

    /**
     * Clean up after pods that are currently dead
     */
    void garbageCollect();

    /**
     * Find orphaned pods that are not tracked in the {@link Controller} and kill them
     */
    void killOrphans();

    /**
     * Returns a class of type T where T is the type of {@link Pod} this runner can run
     *
     * @return the class of T
     */
    Class<T> getType();

}
