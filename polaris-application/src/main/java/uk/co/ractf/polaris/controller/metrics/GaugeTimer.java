package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;

public class GaugeTimer<T> implements Gauge<T> {

    private final Gauge<T> gauge;

    public GaugeTimer(final Gauge<T> gauge) {
        this.gauge = gauge;
    }

    @Override
    public T getValue() {
        return gauge.getValue();
    }
}
