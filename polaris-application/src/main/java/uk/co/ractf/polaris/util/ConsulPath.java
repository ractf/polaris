package uk.co.ractf.polaris.util;

import uk.co.ractf.polaris.api.task.TaskId;

import java.util.StringJoiner;

public class ConsulPath {

    private static final String CHALLENGES = "polaris/challenges";
    private static final String DEPLOYMENTS = "polaris/deployments";
    private static final String NODES = "polaris/nodes";
    private static final String INSTANCES = "polaris/instances";
    private static final String INSTANCE_ALLOCATION = "polaris/instanceallocation";
    private static final String TASKS = "polaris/tasks";

    public static String path(final String... parts) {
        final StringJoiner stringJoiner = new StringJoiner("/");
        for (final String part : parts) {
            stringJoiner.add(part);
        }
        return stringJoiner.toString();
    }

    public static String challenges() {
        return CHALLENGES;
    }

    public static String challenge(final String id) {
        return path(CHALLENGES, id);
    }

    public static String deployments() {
        return DEPLOYMENTS;
    }

    public static String deployment(final String id) {
        return path(DEPLOYMENTS, id);
    }

    public static String deploymentLock(final String id) {
        return path(DEPLOYMENTS, id, "lock");
    }

    public static String nodes() {
        return NODES;
    }

    public static String node(final String id) {
        return path(NODES, id);
    }

    public static String instances() {
        return INSTANCES;
    }

    public static String instance(final String id) {
        return path(INSTANCES, id);
    }

    public static String instanceAllocation() {
        return INSTANCE_ALLOCATION;
    }

    public static String instanceAllocation(final String challenge) {
        return path(INSTANCE_ALLOCATION, challenge);
    }

    public static String tasks() {
        return TASKS;
    }

    public static String task(final String id) {
        return path(TASKS, id);
    }

    public static String task(final TaskId id) {
        return path(TASKS, id.toString());
    }

}
