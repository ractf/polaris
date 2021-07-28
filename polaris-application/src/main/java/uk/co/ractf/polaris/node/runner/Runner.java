package uk.co.ractf.polaris.node.runner;

import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.node.runner.docker.DockerRunner;

import java.util.List;

/**
 * Provides the interface to manage execution of a subclass of {@link Pod}
 *
 * @param <T> The type of pod this runner can run
 */
public interface Runner<T extends Pod> {

    /**
     * Starts a {@link Pod} with given {@link Instance} details
     *
     * @param task     the task
     * @param pod      the pod
     * @param instance instance details
     */
    void startPod(final Task task, final T pod, final Instance instance);

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
    void forceUpdatePod(final Task task, final T pod, final Instance instance);

    /**
     * Update a {@link Pod} if an update is available.
     *
     * @param pod      the pod
     */
    void updatePod(final T pod);

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
     * @param task the task the pod belongs to
     * @param pod  the pod to prepare
     */
    void preparePod(final Task task, final T pod);

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

    /**
     * Gets the name of the runner.
     *
     * @return name
     */
    String getName();

    /**
     * Returns the images that can be used by this runner and are stored locally on the node.
     *
     * @return the locally stored images
     */
    List<String> getImages();

}
