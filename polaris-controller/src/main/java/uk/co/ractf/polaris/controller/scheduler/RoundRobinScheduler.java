package uk.co.ractf.polaris.controller.scheduler;

import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.node.Node;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinScheduler implements Scheduler {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public Node scheduleChallenge(final Challenge challenge, final Collection<Node> nodes) {
        final int hostCounter = counter.incrementAndGet() % nodes.size();
        int i = 0;
        for (final Node node : nodes) {
            if (i == hostCounter) {
                return node;
            }
            i++;
        }
        return nodes.iterator().next();
    }

    @Override
    public Instance descheduleInstance(final Challenge challenge, final Collection<Node> nodes, final Collection<Instance> instances) {
        final int instanceCounter = counter.incrementAndGet() % instances.size();
        int i = 0;
        for (final Instance instance : instances) {
            if (i == instanceCounter) {
                return instance;
            }
            i++;
        }
        return instances.iterator().next();
    }

}
