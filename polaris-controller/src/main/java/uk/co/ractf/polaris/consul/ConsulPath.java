package uk.co.ractf.polaris.consul;

import java.util.StringJoiner;

public class ConsulPath {

    private static final String CHALLENGES = "challenges";
    private static final String DEPLOYMENTS = "deployments";
    private static final String HOSTS = "hosts";
    private static final String INSTANCES = "instances";
    private static final String INSTANCE_ALLOCATION = "instanceallocation";

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

    public static String hosts() {
        return HOSTS;
    }

    public static String host(final String id) {
        return path(HOSTS, id);
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

}
