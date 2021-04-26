package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;
import com.google.inject.Inject;
import uk.co.ractf.polaris.controller.Controller;

public class ChallengesGauge implements Gauge<Integer> {

    private final Controller controller;

    @Inject
    public ChallengesGauge(final Controller controller) {
        this.controller = controller;
    }

    @Override
    public Integer getValue() {
        return controller.getChallenges().size();
    }
}
