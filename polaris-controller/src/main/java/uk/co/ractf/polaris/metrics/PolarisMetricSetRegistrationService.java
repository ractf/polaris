package uk.co.ractf.polaris.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import ru.vyarus.dropwizard.guice.module.installer.feature.eager.EagerSingleton;
import uk.co.ractf.polaris.controller.Controller;

@EagerSingleton
public class PolarisMetricSetRegistrationService {

    @Inject
    public PolarisMetricSetRegistrationService(final MetricRegistry metricRegistry, final Controller controller) {
        metricRegistry.registerAll("polaris", new PolarisMetricSet(controller));
    }

}
