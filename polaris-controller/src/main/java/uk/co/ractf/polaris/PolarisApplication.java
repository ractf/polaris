package uk.co.ractf.polaris;

import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.google.common.util.concurrent.MoreExecutors;
import com.orbitz.consul.Consul;
import com.smoketurner.dropwizard.consul.ConsulBundle;
import com.smoketurner.dropwizard.consul.ConsulFactory;
import com.smoketurner.dropwizard.consul.ribbon.RibbonJerseyClient;
import com.smoketurner.dropwizard.consul.ribbon.RibbonJerseyClientBuilder;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.controller.ConsulController;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.controller.EphemeralController;
import uk.co.ractf.polaris.healthchecks.DockerPingHealthCheck;
import uk.co.ractf.polaris.host.EmbeddedHost;
import uk.co.ractf.polaris.resources.*;
import uk.co.ractf.polaris.security.PolarisAuthenticator;
import uk.co.ractf.polaris.security.PolarisAuthorizer;
import uk.co.ractf.polaris.security.PolarisUser;

import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PolarisApplication extends Application<PolarisConfiguration> {

    private static final Logger log = LoggerFactory.getLogger(PolarisApplication.class);

    private final DockerClient dockerClient;

    private ScheduledExecutorService scheduledExecutorService;
    private ExecutorService executorService;

    private Controller controller;

    public static void main(final String[] args) throws Exception {
        new PolarisApplication().run(args);
    }

    public PolarisApplication() {
        final DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        final DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    @Override
    public void initialize(final Bootstrap<PolarisConfiguration> bootstrap) {
        bootstrap.addBundle(new ConsulBundle<>(getName()) {
            @Override
            public ConsulFactory getConsulFactory(final PolarisConfiguration configuration) {
                return configuration.getConsulFactory();
            }
        });
    }

    @Override
    public void run(final PolarisConfiguration configuration, final Environment environment) throws Exception {
        final Consul consul = configuration.getConsulFactory().build();
        final RibbonJerseyClient loadBalancingClient =
                new RibbonJerseyClientBuilder(environment, consul, configuration.getClient())
                        .build("polaris");

        this.scheduledExecutorService = MoreExecutors.getExitingScheduledExecutorService(
                (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(configuration.getThreadpoolSize()),
                configuration.getThreadpoolTimeoutSeconds(), TimeUnit.SECONDS);
        this.executorService = MoreExecutors.getExitingExecutorService(
                (ThreadPoolExecutor) Executors.newFixedThreadPool(configuration.getTaskThreadpoolSize()),
                configuration.getThreadpoolTimeoutSeconds(), TimeUnit.SECONDS);

        if (configuration.getControllerType().equals("ephemeral")) {
            this.controller = new EphemeralController(configuration, scheduledExecutorService, executorService);
        } else if (configuration.getControllerType().equals("consul")) {
            this.controller = new ConsulController(configuration, consul, scheduledExecutorService, executorService);
        }

        for (final String hostname : configuration.getHosts()) {
            if ("embedded".equals(hostname)) {
                controller.addHost(new EmbeddedHost(controller, dockerClient, scheduledExecutorService, executorService, configuration));
            }
        }

        environment.healthChecks().register("dockerping", new DockerPingHealthCheck(dockerClient));

        environment.metrics().registerAll(new GarbageCollectorMetricSet());
        environment.metrics().registerAll(new MemoryUsageGaugeSet());

        environment.jersey().register(new ChallengeResource(controller));
        environment.jersey().register(new DeploymentResource(controller));
        environment.jersey().register(new InstanceResource(controller));
        environment.jersey().register(new InstanceAllocationResource(controller));
        environment.jersey().register(new HostResource(controller));

        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<PolarisUser>()
                .setAuthenticator(new PolarisAuthenticator(configuration))
                .setAuthorizer(new PolarisAuthorizer(configuration))
                .setRealm("POLARIS")
                .buildAuthFilter()
        ));
        environment.jersey().register(RolesAllowedDynamicFeature.class);

        OpenAPI openAPI = new OpenAPI();
        Info info = new Info()
                .title("RACTF Polaris")
                .description("RACTF Polaris Controller API")
                .contact(new Contact().email("admins@ractf.co.uk"));

        openAPI.info(info);
        SwaggerConfiguration openAPIConfig = new SwaggerConfiguration()
                .openAPI(openAPI)
                .prettyPrint(true)
                .resourcePackages(Stream.of("uk.co.ractf.polaris.resources")
                        .collect(Collectors.toSet()));
        environment.jersey().register(new OpenApiResource()
                .openApiConfiguration(openAPIConfig));
    }

    @Override
    public String getName() {
        return "polaris";
    }

}
