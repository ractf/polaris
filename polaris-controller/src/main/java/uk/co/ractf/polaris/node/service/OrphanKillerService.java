package uk.co.ractf.polaris.node.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.co.ractf.polaris.PolarisConfiguration;
import uk.co.ractf.polaris.node.runner.Runner;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class OrphanKillerService extends AbstractScheduledService {

    private final Set<Runner> runners;
    private final PolarisConfiguration configuration;

    @Inject
    public OrphanKillerService(final Set<Runner> runners, final PolarisConfiguration configuration) {
        this.runners = runners;
        this.configuration = configuration;
    }

    @Override
    protected void runOneIteration() throws Exception {
        if (configuration.isKillOrphans()) {
            for (final Runner runner : runners) {
                CompletableFuture.runAsync(runner::killOrphans);
            }
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 5, TimeUnit.SECONDS);
    }
}
