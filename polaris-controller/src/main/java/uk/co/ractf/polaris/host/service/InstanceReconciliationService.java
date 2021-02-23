package uk.co.ractf.polaris.host.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.PolarisConfiguration;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.host.Host;
import uk.co.ractf.polaris.runner.Runner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class InstanceReconciliationService extends AbstractScheduledService {

    private static final Logger log = LoggerFactory.getLogger(InstanceReconciliationService.class);

    private final Controller controller;
    private final Host host;
    private final Map<Class<? extends Pod>, Runner<? extends Pod>> runners = new HashMap<>();
    private final PolarisConfiguration polarisConfiguration;
    private final LoadingCache<String, String> recentlyStartedInstances = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
                @Override
                public String load(final String s) {
                    return s;
                }
            });

    private final Set<Runner> runnerSet;

    @Inject
    public InstanceReconciliationService(final Controller controller,
                                         final Host host,
                                         final Set<Runner> runners,
                                         final PolarisConfiguration polarisConfiguration) {
        this.controller = controller;
        this.host = host;
        this.runnerSet = runners;
        this.polarisConfiguration = polarisConfiguration;
    }

    @Override
    protected void startUp() throws Exception {
        for (Runner<?> runner : runnerSet) {
            this.runners.put(runner.getType(), runner);
        }
        super.startUp();
    }

    @SuppressWarnings("unchecked")
    private <T extends Pod> Runner<Pod> getRunner(final T pod) {
        return (Runner<Pod>) runners.get(pod.getClass());
    }

    @Override
    protected void runOneIteration() {
        try {
            for (final Map.Entry<String, Instance> entry : host.getInstances().entrySet()) {
                final Instance instance = entry.getValue();
                final Challenge challenge = controller.getChallengeFromDeployment(instance.getDeploymentID());
                if (challenge == null) {
                    continue;
                }
                for (final Pod pod : challenge.getPods()) {
                    if (recentlyStartedInstances.getIfPresent(pod.getID() + instance.getID()) != null) {
                        continue;
                    }
                    if (getRunner(pod).canStartPod(pod)) {
                        log.debug("ensuring pod started {} {}", pod.getID(), instance.getID());
                        recentlyStartedInstances.put(pod.getID() + instance.getID(), "");
                        CompletableFuture.runAsync(() -> {
                            if (!getRunner(pod).isPodStarted(pod, instance)) {
                                log.info("telling runner to start instance of {} for {}", pod.getID(), instance.getID());
                                getRunner(pod).startPod(pod, instance);
                            }
                        });
                    } else {
                        getRunner(pod).preparePod(pod);
                    }
                }
            }
        } catch (Exception e) {
            log.error("error", e);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, polarisConfiguration.getReconciliationTickFrequency(), TimeUnit.MILLISECONDS);
    }

}
