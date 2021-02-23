package uk.co.ractf.polaris.healthchecks;

import com.codahale.metrics.health.HealthCheck;
import com.github.dockerjava.api.DockerClient;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.inject.Inject;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DockerPingHealthCheck extends HealthCheck {

    private final DockerClient dockerClient;

    @Inject
    public DockerPingHealthCheck(final DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    protected Result check() throws Exception {
        try {
            SimpleTimeLimiter.create(Executors.newCachedThreadPool())
                    .callWithTimeout(dockerClient.pingCmd()::exec, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            return Result.unhealthy("Docker ping failed or timed out");
        }
        return Result.healthy();
    }

}
