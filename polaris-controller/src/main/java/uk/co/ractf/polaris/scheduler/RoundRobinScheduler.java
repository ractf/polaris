package uk.co.ractf.polaris.scheduler;

import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.host.Host;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinScheduler implements Scheduler {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public Host scheduleChallenge(final Challenge challenge, final Collection<Host> hosts) {
        final int hostCounter = counter.incrementAndGet() % hosts.size();
        int i = 0;
        for (final Host host : hosts) {
            if (i == hostCounter) {
                return host;
            }
            i++;
        }
        return hosts.iterator().next();
    }

    @Override
    public Instance descheduleInstance(final Challenge challenge, final Collection<Host> hosts, final Collection<Instance> instances) {
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
