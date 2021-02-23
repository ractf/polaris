package uk.co.ractf.polaris.host.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.co.ractf.polaris.runner.Runner;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Singleton
public class GarbageCollectionService extends AbstractScheduledService {

    private final Set<Runner> runners;

    @Inject
    public GarbageCollectionService(final Set<Runner> runners) {
        this.runners = runners;
    }


    @Override
    protected void runOneIteration() throws Exception {
        for (final Runner runner : runners) {
            runner.garbageCollect();
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(60, 60, TimeUnit.MINUTES);
    }
}
