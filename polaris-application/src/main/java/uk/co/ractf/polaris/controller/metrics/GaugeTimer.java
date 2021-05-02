package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.Gauge;

public class GaugeTimer<T> implements Gauge<T> {

    private final Gauge<T> gauge;

    public GaugeTimer(final Gauge<T> gauge) {
        this.gauge = gauge;
    }

    @Override
    public T getValue() {
        final var start = System.currentTimeMillis();
        final var value = gauge.getValue();
        final var end = System.currentTimeMillis();
        System.out.println("Calling " + gauge.getClass().getSimpleName() + " took " + (end - start) + "ms");
        return value;
    }
}
