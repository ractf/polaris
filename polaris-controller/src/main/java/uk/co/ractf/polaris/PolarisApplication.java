package uk.co.ractf.polaris;

import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.smoketurner.dropwizard.consul.ConsulBundle;
import com.smoketurner.dropwizard.consul.ConsulFactory;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.GuiceBundle;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PolarisApplication extends Application<PolarisConfiguration> {

    private static final Logger log = LoggerFactory.getLogger(PolarisApplication.class);

    public static void main(final String[] args) throws Exception {
        new PolarisApplication().run(args);
    }

    public PolarisApplication() {
    }

    @Override
    public void initialize(final Bootstrap<PolarisConfiguration> bootstrap) {
        bootstrap.addBundle(new ConsulBundle<>(getName()) {
            @Override
            public ConsulFactory getConsulFactory(final PolarisConfiguration configuration) {
                return configuration.getConsulFactory();
            }
        });

        bootstrap.addBundle(GuiceBundle.builder()
                .modules(new PolarisModule())
                //dropwizard-guicey's autoconfig will instantiate *all* implementations of controller and host if we let it see them
                .enableAutoConfig("uk.co.ractf.polaris.consul",
                        "uk.co.ractf.polaris.healthchecks",
                        "uk.co.ractf.polaris.instanceallocation",
                        "uk.co.ractf.polaris.replication",
                        "uk.co.ractf.polaris.resources",
                        "uk.co.ractf.polaris.runner",
                        "uk.co.ractf.polaris.scheduler",
                        "uk.co.ractf.polaris.security",
                        "uk.co.ractf.polaris.util")
                .build());

        CollectorRegistry.defaultRegistry.register(new DropwizardExports(bootstrap.getMetricRegistry()));

        final Server server = new Server(8082);
        final ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new MetricsServlet(bootstrap.getMetricRegistry())), "/prometheus");
        try {
            server.start();
        } catch (final Exception e) {
            log.error("Could not start prometheus integration", e);
        }
    }

    @Override
    public void run(final PolarisConfiguration configuration, final Environment environment) {
        environment.metrics().registerAll(new GarbageCollectorMetricSet());
        environment.metrics().registerAll(new MemoryUsageGaugeSet());

        final OpenAPI openAPI = new OpenAPI();
        final Info info = new Info()
                .title("RACTF Polaris")
                .description("RACTF Polaris Controller API")
                .contact(new Contact().email("admins@ractf.co.uk"));

        openAPI.info(info);
        final SwaggerConfiguration openAPIConfig = new SwaggerConfiguration()
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
