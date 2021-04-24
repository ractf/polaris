package uk.co.ractf.polaris.metrics;

import com.codahale.metrics.Gauge;
import com.google.inject.Inject;
import uk.co.ractf.polaris.controller.Controller;

public class DeploymentsGauge implements Gauge<Integer> {

    private final Controller controller;

    @Inject
    public DeploymentsGauge(final Controller controller) {
        this.controller = controller;
    }

    @Override
    public Integer getValue() {
        return controller.getDeployments().size();
    }
}
