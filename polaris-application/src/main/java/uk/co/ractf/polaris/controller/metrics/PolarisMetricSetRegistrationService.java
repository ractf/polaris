package uk.co.ractf.polaris.controller.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import ru.vyarus.dropwizard.guice.module.installer.feature.eager.EagerSingleton;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.state.ClusterState;

@EagerSingleton
public class PolarisMetricSetRegistrationService {

    @Inject
    public PolarisMetricSetRegistrationService(final MetricRegistry metricRegistry, final ClusterState clusterState) {
        metricRegistry.registerAll("polaris", new PolarisMetricSet(clusterState));
    }

}
