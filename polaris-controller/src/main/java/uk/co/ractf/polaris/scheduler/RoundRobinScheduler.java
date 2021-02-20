package uk.co.ractf.polaris.scheduler;

import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.host.Host;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinScheduler implements Scheduler {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public Host scheduleChallenge(final Challenge challenge, final Collection<Host> hosts) {
        final int hostCounter = counter.incrementAndGet();
        int i = 0;
        for (final Host host : hosts) {
            if (i == hostCounter) {
                return host;
            }
            i++;
        }
        return hosts.iterator().next();
    }

}
