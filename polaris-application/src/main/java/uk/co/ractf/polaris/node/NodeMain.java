package uk.co.ractf.polaris.node;

import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.google.common.collect.ObjectArrays;
import com.smoketurner.dropwizard.consul.ConsulBundle;
import com.smoketurner.dropwizard.consul.ConsulFactory;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.dhatim.dropwizard.prometheus.PrometheusBundle;
import ru.vyarus.dropwizard.guice.GuiceBundle;

import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO: Does this *really* need to be a dropwizard app?
public class NodeMain extends Application<NodeConfiguration> {

    public static void main(final String[] args) throws Exception {
        new NodeMain().run(ObjectArrays.concat("server", args));
    }

    @Override
    public void initialize(final Bootstrap<NodeConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false))
        );

        bootstrap.addBundle(new ConsulBundle<>(getName()) {
            @Override
            public ConsulFactory getConsulFactory(final NodeConfiguration configuration) {
                return configuration.getConsulFactory();
            }
        });

        bootstrap.addBundle(GuiceBundle.builder()
                .modules(new NodeModule())
                //dropwizard-guicey's autoconfig will instantiate *all* implementations of controller and host if we let it see them
                .enableAutoConfig(
                        "uk.co.ractf.polaris.consul",
                        "uk.co.ractf.polaris.node.metrics",
                        "uk.co.ractf.polaris.node.runner",
                        "uk.co.ractf.polaris.node.service"
                )
                .build());

        bootstrap.addBundle(new PrometheusBundle());
    }

    @Override
    public void run(final NodeConfiguration configuration, final Environment environment) {
        environment.metrics().registerAll(new GarbageCollectorMetricSet());
        environment.metrics().registerAll(new MemoryUsageGaugeSet());

        final OpenAPI openAPI = new OpenAPI();
        final Info info = new Info()
                .title("RACTF Polaris")
                .description("RACTF Polaris Node API")
                .contact(new Contact().email("admins@ractf.co.uk"));

        openAPI.info(info);
        final SwaggerConfiguration openAPIConfig = new SwaggerConfiguration()
                .openAPI(openAPI)
                .prettyPrint(true)
                .resourcePackages(Stream.of("uk.co.ractf.polaris.controller.resources")
                        .collect(Collectors.toSet()));
        environment.jersey().register(new OpenApiResource()
                .openApiConfiguration(openAPIConfig));
    }
}
