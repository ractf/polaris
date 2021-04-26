package uk.co.ractf.polaris.controller;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.kv.Operation;
import com.orbitz.consul.model.kv.Verb;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.controller.instanceallocation.EphemeralInstanceAllocator;
import uk.co.ractf.polaris.controller.instanceallocation.InstanceAllocator;
import uk.co.ractf.polaris.controller.service.ControllerServices;
import uk.co.ractf.polaris.node.Node;
import uk.co.ractf.polaris.state.ClusterState;
import uk.co.ractf.polaris.util.ConsulPath;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ConsulController implements Controller, Managed {

    private static final Logger log = LoggerFactory.getLogger(ConsulController.class);

    private final ControllerConfiguration config;
    private final Consul consul;
    private final Map<String, Node> hosts = new ConcurrentHashMap<>();
    private final InstanceAllocator instanceAllocator;
    private final Set<Service> services;
    private final ClusterState clusterState;

    @Inject
    public ConsulController(final ControllerConfiguration config,
                            final Consul consul,
                            @ControllerServices final Set<Service> services,
                            final ClusterState clusterState) {
        this.config = config;
        this.consul = consul;
        this.instanceAllocator = new EphemeralInstanceAllocator(clusterState);
        this.services = services;
        this.clusterState = clusterState;
    }

    @Override
    public void start() {
        for (final Service service : services) {
            service.startAsync();
        }
    }

    @Override
    public void stop() {
        for (final Service service : services) {
            service.stopAsync();
        }
    }

    @Override
    public void createChallenge(final Challenge challenge) {
        Preconditions.checkArgument(!challenge.getId().isBlank(), "Challenge id cannot be blank.");
        if (clusterState.getChallenge(challenge.getId()) != null) {
            throw new IllegalArgumentException("Challenge with id " + challenge.getId() + " already exists.");
        }
        consul.keyValueClient().performTransaction(
                Operation.builder(Verb.SET)
                        .key(ConsulPath.challenge(challenge.getId()))
                        .value(challenge.toJsonString())
                        .build());
    }

    @Override
    public InstanceAllocator getInstanceAllocator() {
        return instanceAllocator;
    }

}
