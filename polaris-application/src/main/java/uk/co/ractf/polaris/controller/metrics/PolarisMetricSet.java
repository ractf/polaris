package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.inject.Inject;
import uk.co.ractf.polaris.controller.Controller;

import java.util.HashMap;
import java.util.Map;

public class PolarisMetricSet implements MetricSet {

    private final Controller controller;

    @Inject
    public PolarisMetricSet(final Controller controller) {
        this.controller = controller;
    }

    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> metrics = new HashMap<>();
        metrics.put("challenges.count", new ChallengesGauge(controller));
        metrics.put("deployments.count", new DeploymentsGauge(controller));
        metrics.put("hosts.count", new HostsGauge(controller));
        metrics.put("instances.count", new InstancesGauge(controller));
        return metrics;
    }
}
