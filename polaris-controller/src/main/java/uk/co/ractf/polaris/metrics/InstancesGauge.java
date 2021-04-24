package uk.co.ractf.polaris.metrics;

import com.codahale.metrics.Gauge;
import uk.co.ractf.polaris.controller.Controller;

public class InstancesGauge implements Gauge<Integer> {

    private final Controller controller;

    public InstancesGauge(final Controller controller) {
        this.controller = controller;
    }

    @Override
    public Integer getValue() {
        int total = 0;
        for (final String deployment : controller.getDeployments().keySet()) {
            total += controller.getInstancesForDeployment(deployment).size();
        }
        return total;
    }
}
