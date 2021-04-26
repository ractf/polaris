package uk.co.ractf.polaris.controller;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.controller.instanceallocation.EphemeralInstanceAllocator;
import uk.co.ractf.polaris.controller.instanceallocation.InstanceAllocator;
import uk.co.ractf.polaris.controller.service.ControllerServices;
import uk.co.ractf.polaris.node.Node;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Singleton
@Deprecated
public class EphemeralController implements Controller, Managed {

    private static final Logger log = LoggerFactory.getLogger(EphemeralController.class);

    private final Map<String, Challenge> challenges = new ConcurrentHashMap<>();
    private final Map<String, Deployment> deployments = new ConcurrentHashMap<>();
    private final Multimap<String, Instance> deploymentInstances = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private final Map<String, Semaphore> deploymentLocks = new ConcurrentHashMap<>();
    private final Map<String, Instance> instances = new ConcurrentHashMap<>();
    private final Map<String, Node> hosts = new ConcurrentHashMap<>();
    private final Set<Service> services;
    private final InstanceAllocator instanceAllocator;

    @Inject
    public EphemeralController(@ControllerServices final Set<Service> services, final ClusterState clusterState) {
        this.services = services;
        this.instanceAllocator = new EphemeralInstanceAllocator(clusterState);
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
        challenges.put(challenge.getId(), challenge);
    }

    @Override
    public InstanceAllocator getInstanceAllocator() {
        return instanceAllocator;
    }


}
