package uk.co.ractf.polaris.controller;

import com.codahale.metrics.MetricRegistry;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.orbitz.consul.Consul;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.util.Duration;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import uk.co.ractf.polaris.PolarisConfiguration;
import uk.co.ractf.polaris.annotation.ExcludeFromGeneratedReport;
import uk.co.ractf.polaris.controller.service.ControllerServiceModule;
import uk.co.ractf.polaris.host.EmbeddedNode;
import uk.co.ractf.polaris.host.Node;
import uk.co.ractf.polaris.host.service.HostServiceModule;
import uk.co.ractf.polaris.host.runner.DockerRunner;
import uk.co.ractf.polaris.host.runner.Runner;
import uk.co.ractf.polaris.controller.scheduler.RoundRobinScheduler;
import uk.co.ractf.polaris.controller.scheduler.Scheduler;

import java.util.concurrent.ScheduledExecutorService;

@ExcludeFromGeneratedReport
public class ControllerModule extends DropwizardAwareModule<PolarisConfiguration> {

    private final Bootstrap<PolarisConfiguration> bootstrap;

    public ControllerModule(final Bootstrap<PolarisConfiguration> bootstrap) {
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

        final DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        final DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        bind(DockerClient.class).toInstance(DockerClientImpl.getInstance(config, httpClient));

        final Multibinder<Runner> runnerBinder = Multibinder.newSetBinder(binder(), Runner.class);
        runnerBinder.addBinding().to(DockerRunner.class);

        bind(Node.class).to(EmbeddedNode.class).in(Singleton.class);
        bind(Scheduler.class).to(RoundRobinScheduler.class);

        if ("consul".equals(configuration().getControllerType())) {
            bind(Controller.class).to(ConsulController.class).in(Singleton.class);
        } else {
            bind(Controller.class).to(EphemeralController.class).in(Singleton.class);
        }

        bind(MetricRegistry.class).toInstance(bootstrap.getMetricRegistry());

        install(new HostServiceModule());
        install(new ControllerServiceModule());

    }
}
