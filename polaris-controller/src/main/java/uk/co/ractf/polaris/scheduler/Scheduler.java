package uk.co.ractf.polaris.scheduler;

import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.host.Host;

import java.util.Collection;

public interface Scheduler {

    Host scheduleChallenge(final Challenge challenge, final Collection<Host> hosts);

}
