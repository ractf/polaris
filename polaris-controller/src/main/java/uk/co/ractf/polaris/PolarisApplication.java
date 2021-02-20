package uk.co.ractf.polaris;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.google.common.util.concurrent.MoreExecutors;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.controller.EphemeralController;
import uk.co.ractf.polaris.healthchecks.DockerPingHealthCheck;
import uk.co.ractf.polaris.host.EmbeddedHost;
import uk.co.ractf.polaris.resources.ChallengeResource;
import uk.co.ractf.polaris.resources.DeploymentResource;
import uk.co.ractf.polaris.resources.InstanceAllocationResource;
import uk.co.ractf.polaris.resources.InstanceResource;

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
    public void run(final PolarisConfiguration configuration, final Environment environment) throws Exception {
        this.scheduledExecutorService = MoreExecutors.getExitingScheduledExecutorService(
                (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(configuration.getThreadpoolSize()),
                configuration.getThreadpoolTimeoutSeconds(), TimeUnit.SECONDS);
        this.executorService = MoreExecutors.getExitingExecutorService(
                (ThreadPoolExecutor) Executors.newFixedThreadPool(configuration.getTaskThreadpoolSize()),
                configuration.getThreadpoolTimeoutSeconds(), TimeUnit.SECONDS);

        if (configuration.getControllerType().equals("ephemeral")) {
            this.controller = new EphemeralController(scheduledExecutorService, executorService, configuration);
        }

        for (final String hostname : configuration.getHosts()) {
            if ("embedded".equals(hostname)) {
                controller.addHost(new EmbeddedHost(controller, dockerClient, scheduledExecutorService, executorService));
            }
        }

        environment.healthChecks().register("dockerping", new DockerPingHealthCheck(dockerClient));

        environment.jersey().register(new ChallengeResource(controller));
        environment.jersey().register(new DeploymentResource(controller));
        environment.jersey().register(new InstanceResource(controller));
        environment.jersey().register(new InstanceAllocationResource(controller));

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
