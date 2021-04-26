package uk.co.ractf.polaris.controller;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Singleton;
import com.orbitz.consul.Consul;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.util.Duration;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import uk.co.ractf.polaris.api.annotation.ExcludeFromGeneratedTestReport;
import uk.co.ractf.polaris.controller.scheduler.RoundRobinScheduler;
import uk.co.ractf.polaris.controller.scheduler.Scheduler;
import uk.co.ractf.polaris.controller.service.ControllerServiceModule;
import uk.co.ractf.polaris.node.EmbeddedNode;
import uk.co.ractf.polaris.node.Node;
import uk.co.ractf.polaris.state.ClusterState;
import uk.co.ractf.polaris.state.ConsulState;

import java.util.concurrent.ScheduledExecutorService;

@ExcludeFromGeneratedTestReport
public class ControllerModule extends DropwizardAwareModule<ControllerConfiguration> {

    private final Bootstrap<ControllerConfiguration> bootstrap;

    public ControllerModule(final Bootstrap<ControllerConfiguration> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void configure() {
        bind(ScheduledExecutorService.class).toInstance(environment().lifecycle()
                .scheduledExecutorService("Polaris-ExecutorService-%d")
                .threads(configuration().getThreadpoolSize())
                .shutdownTime(Duration.seconds(configuration().getThreadpoolTimeoutSeconds()))
                .build());
        bind(Consul.class).toInstance(configuration().getConsulFactory().build());

        bind(Node.class).to(EmbeddedNode.class).in(Singleton.class);
        bind(Scheduler.class).to(RoundRobinScheduler.class);
        bind(Controller.class).to(ConsulController.class).in(Singleton.class);
        bind(MetricRegistry.class).toInstance(bootstrap.getMetricRegistry());
        bind(ClusterState.class).to(ConsulState.class);

        install(new ControllerServiceModule());

    }
}
