package uk.co.ractf.polaris.controller.scheduler;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
public class DefaultScheduler implements Scheduler, Managed {

    private static final Logger log = LoggerFactory.getLogger(DefaultScheduler.class);

    private final SchedulingAlgorithm schedulingAlgorithm;
    private final ClusterState clusterState;
    private final Set<InstanceDecoratorPlugin> instanceDecoratorPlugins;
    private final MetricRegistry metricRegistry;

    private Timer nodeSelectionLatency;
    private Timer totalLatency;
    private Meter failMeter;
    private Meter successMeter;
    private Meter attemptMeter;

    @Inject
    public DefaultScheduler(final SchedulingAlgorithm schedulingAlgorithm, final ClusterState clusterState,
                            final Set<InstanceDecoratorPlugin> instanceDecoratorPlugins, final MetricRegistry metricRegistry) {
        this.schedulingAlgorithm = schedulingAlgorithm;
        this.clusterState = clusterState;
        this.instanceDecoratorPlugins = instanceDecoratorPlugins;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public void start() throws Exception {
        final var timers = metricRegistry.getTimers(MetricFilter.startsWith("polaris.scheduler"));
        timers.keySet().forEach(System.out::println);
        nodeSelectionLatency = timers.get("polaris.scheduler.latency.nodeselection");
        totalLatency = timers.get("polaris.scheduler.latency.total");

        final var meters = metricRegistry.getMeters(MetricFilter.ALL);
        failMeter = meters.get("polaris.scheduler.fail");
        successMeter = meters.get("polaris.scheduler.success");
        attemptMeter = meters.get("polaris.scheduler.attempts");
    }

    @Override
    public void schedule(final Task task) {
        attemptMeter.mark();
        final var startTime = System.nanoTime();
        final var scheduleResult = schedulingAlgorithm.schedule(task);
        nodeSelectionLatency.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        if (!scheduleResult.isSuccessful()) {
            failMeter.mark();
            log.warn("Failed to schedule {}", task.getId());
            return;
        }
        successMeter.mark();

        final var node = scheduleResult.getNode();
        var instance = new Instance(UUID.randomUUID().toString(), task.getId(), node.getId(), new ArrayList<>(), new HashMap<>());
        for (final var plugin : instanceDecoratorPlugins) {
            instance = plugin.decorate(instance, task, node);
        }
        clusterState.setInstance(instance);

        totalLatency.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        successMeter.mark();
    }

    @Override
    public void deschedule(final Task task) {

    }

    @Override
    public void stop() {}
}
