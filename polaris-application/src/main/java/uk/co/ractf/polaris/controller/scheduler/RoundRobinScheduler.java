package uk.co.ractf.polaris.controller.scheduler;

import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.task.Task;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinScheduler implements Scheduler {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public NodeInfo scheduleTask(final Task challenge, final Collection<NodeInfo> nodes) {
        final var hostCounter = counter.incrementAndGet() % nodes.size();
        var i = 0;
        for (final var node : nodes) {
            if (i == hostCounter) {
                return node;
            }
            i++;
        }
        return nodes.iterator().next();
    }

    @Override
    public Instance descheduleInstance(final Task challenge, final Collection<NodeInfo> nodes, final Collection<Instance> instances) {
        final var instanceCounter = counter.incrementAndGet() % instances.size();
        var i = 0;
        for (final var instance : instances) {
            if (i == instanceCounter) {
                return instance;
            }
            i++;
        }
        return instances.iterator().next();
    }

}
