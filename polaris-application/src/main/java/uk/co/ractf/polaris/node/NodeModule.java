package uk.co.ractf.polaris.node;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.orbitz.consul.Consul;
import io.dropwizard.util.Duration;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import uk.co.ractf.polaris.node.runner.RunnerModule;
import uk.co.ractf.polaris.node.service.NodeServiceModule;
import uk.co.ractf.polaris.state.ClusterState;
import uk.co.ractf.polaris.state.ConsulState;

import java.util.concurrent.ScheduledExecutorService;

public class NodeModule extends DropwizardAwareModule<NodeConfiguration> {
    @Override
    protected void configure() {
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
        bind(Node.class).to(ConsulNode.class);
        bind(ClusterState.class).to(ConsulState.class);

        install(new NodeServiceModule());
        install(new RunnerModule());
    }
}
