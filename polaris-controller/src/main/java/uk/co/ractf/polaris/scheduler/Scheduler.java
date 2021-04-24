package uk.co.ractf.polaris.scheduler;

import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.host.Host;

import java.util.Collection;

/**
 * Interface to schedule {@link Challenge}s onto {@link Host}s
 */
public interface Scheduler {

    /**
     * Return the best {@link Host} for this {@link Challenge} to run on out of the {@link Collection}
     *
     * @param challenge the challenge
     * @param hosts     the hosts
     * @return the host it should be scheduled on
     */
    Host scheduleChallenge(final Challenge challenge, final Collection<Host> hosts);

    /**
     * Returns the best {@link Instance} to be descheduled out of the current set
     *
     * @param challenge the {@link Challenge}
     * @param hosts     the clusters {@link Host}s
     * @param instances the instance
     * @return the instance to deschedule
     */
    Instance descheduleInstance(final Challenge challenge, final Collection<Host> hosts, final Collection<Instance> instances);

}
