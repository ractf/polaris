package uk.co.ractf.polaris.host.metrics;

import com.github.dockerjava.api.DockerClient;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
public class DockerPingHealthCheck extends NamedHealthCheck {

    private final DockerClient dockerClient;

    @Inject
    public DockerPingHealthCheck(final DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    protected Result check() {
        try {
            SimpleTimeLimiter.create(Executors.newCachedThreadPool())
                    .callWithTimeout(dockerClient.pingCmd()::exec, 5, TimeUnit.SECONDS);
        } catch (final Exception exception) {
            return Result.unhealthy("Docker ping failed or timed out");
        }
        return Result.healthy();
    }

    @Override
    public String getName() {
        return "dockerping";
    }
}
