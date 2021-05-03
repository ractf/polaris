package uk.co.ractf.polaris.controller;

import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.controller.instanceallocation.EphemeralInstanceAllocator;
import uk.co.ractf.polaris.controller.instanceallocation.InstanceAllocator;
import uk.co.ractf.polaris.controller.service.ControllerServices;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Set;

@Singleton
public class ConsulController implements Controller, Managed {

    private static final Logger log = LoggerFactory.getLogger(ConsulController.class);

    private final InstanceAllocator instanceAllocator;
    private final Set<Service> services;

    @Inject
    public ConsulController(@ControllerServices final Set<Service> services,
                            final ClusterState clusterState) {
        this.instanceAllocator = new EphemeralInstanceAllocator(clusterState);
        this.services = services;
    }

    @Override
    public void start() {
        for (final var service : services) {
            System.out.println(service.getClass().getName());
            service.startAsync();
        }
    }

    @Override
    public void stop() {
        for (final var service : services) {
            service.stopAsync();
        }
    }

    @Override
    public InstanceAllocator getInstanceAllocator() {
        return instanceAllocator;
    }

}
