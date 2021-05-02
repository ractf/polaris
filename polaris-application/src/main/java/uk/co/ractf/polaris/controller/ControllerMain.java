package uk.co.ractf.polaris.controller;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.google.common.collect.ObjectArrays;
import com.smoketurner.dropwizard.consul.ConsulBundle;
import com.smoketurner.dropwizard.consul.ConsulFactory;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.dhatim.dropwizard.prometheus.PrometheusBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import uk.co.ractf.polaris.api.annotation.ExcludeFromGeneratedTestReport;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExcludeFromGeneratedTestReport
public class ControllerMain extends Application<ControllerConfiguration> {

    private static final Logger log = LoggerFactory.getLogger(ControllerMain.class);

    public ControllerMain() {
    }

    public static void main(final String[] args) throws Exception {
        new ControllerMain().run(ObjectArrays.concat("server", args));
    }

    @Override
    public void initialize(final Bootstrap<ControllerConfiguration> bootstrap) {
        bootstrap.addBundle(new ConsulBundle<>(getName()) {
            @Override
            public ConsulFactory getConsulFactory(final ControllerConfiguration configuration) {
                return configuration.getConsulFactory();
            }
        });

        bootstrap.addBundle(GuiceBundle.builder()
                .modules(new ControllerModule(bootstrap))
                //dropwizard-guicey's autoconfig will instantiate *all* implementations of controller and host if we let it see them
                .enableAutoConfig(
                        "uk.co.ractf.polaris.consul",
                        "uk.co.ractf.polaris.host.healthchecks",
                        "uk.co.ractf.polaris.controller.instanceallocation",
                        "uk.co.ractf.polaris.controller.metrics",
                        "uk.co.ractf.polaris.controller.replication",
                        "uk.co.ractf.polaris.controller.resources",
                        "uk.co.ractf.polaris.host.runner",
                        "uk.co.ractf.polaris.controller.scheduler",
                        "uk.co.ractf.polaris.security",
                        "uk.co.ractf.polaris.util")
                .build());

        bootstrap.addBundle(new PrometheusBundle());
    }

    @Override
    public void run(final ControllerConfiguration configuration, final Environment environment) {
        environment.metrics().registerAll(new GarbageCollectorMetricSet());
        environment.metrics().registerAll(new MemoryUsageGaugeSet());

        final var openAPI = new OpenAPI();
        final var info = new Info()
                .title("RACTF Polaris")
                .description("RACTF Polaris Controller API")
                .contact(new Contact().email("admins@ractf.co.uk"));

        openAPI.info(info);
        final var openAPIConfig = new SwaggerConfiguration()
                .openAPI(openAPI)
                .prettyPrint(true)
                .resourcePackages(Stream.of("uk.co.ractf.polaris.controller.resources")
                        .collect(Collectors.toSet()));
        environment.jersey().register(new OpenApiResource()
                .openApiConfiguration(openAPIConfig));
    }

    @Override
    public String getName() {
        return "polaris";
    }

}
