package uk.co.ractf.polaris.controller.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.annotation.ExcludeFromGeneratedTestReport;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instance.InstancePortBinding;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.pod.PodWithPorts;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.controller.PortAllocator;
import uk.co.ractf.polaris.controller.replication.ReplicationController;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
public class TaskScaleReconciliationService extends AbstractScheduledService {

    private static final Logger log = LoggerFactory.getLogger(TaskScaleReconciliationService.class);

    private final ClusterState clusterState;
    private final uk.co.ractf.polaris.controller.scheduler.Scheduler scheduler;
    private final ControllerConfiguration config;

    @Inject
    public TaskScaleReconciliationService(final ClusterState controller,
                                          final uk.co.ractf.polaris.controller.scheduler.Scheduler scheduler,
                                          final ControllerConfiguration config) {
        this.clusterState = controller;
        this.scheduler = scheduler;
        this.config = config;
    }

    @Override
    protected void runOneIteration() {
        try {
            final var tasks = clusterState.getTasks();
            log.trace("Starting task scale reconciliation tick");

            final var nodes = clusterState.getNodes().values();
            if (nodes.size() == 0) {
                log.debug("No nodes detected, skipping reconciliation");
                return;
            }

            for (final Task task : tasks.values()) {
                log.debug("Attempting to reconcile deployment {}", task.getId());
                if (!clusterState.lockTask(task)) {
                    log.debug("Could not obtain lock for {}", task.getId());
                    continue;
                }
                final var instances = new ArrayList<>(clusterState.getInstancesOfTask(task.getId()).values());

                final var scaleAmount = ReplicationController.create(task.getReplication()).getScaleAmount(instances, clusterState);
                log.debug("Replication: static {} {}", ((StaticReplication) task.getReplication()).getAmount(), instances.size());
                log.debug("Scaling required for {}: {}", task.getId(), scaleAmount);

                if (scaleAmount > 0) {
                    log.info("Scheduling instances: {} of {}", scaleAmount, task.getId());
                    for (var i = 0; i < scaleAmount; i++) {
                        final var node = scheduler.scheduleTask(task, nodes);
                        log.info("Scheduled instance of {} onto {}", task.getId(), node.getId());

                        final var instance = createInstance(task, node);
                        clusterState.setInstance(instance);
                        log.info("Instance of {} successfully registered on {}", instance.getId(), node.getId());
                    }
                } else {
                    for (var i = 0; i > scaleAmount; i--) {
                        final var instance = scheduler.descheduleInstance(task, nodes, instances);
                        clusterState.deleteInstance(instance);
                    }
                }
                clusterState.unlockTask(task);
            }
        } catch (final Exception e) {
            log.error("Error reconciling deployments", e);
        }
    }

    private Instance createInstance(final Task task, final NodeInfo node) {
        final var portAllocator = new PortAllocator(config.getMinPort(), config.getMaxPort(), node.getPortAllocations());
        final var portBindings = getInstancePortBindings(task, node, portAllocator);
        return new Instance(UUID.randomUUID().toString(), task.getId(), node.getId(), portBindings, new HashMap<>());
    }

    @NotNull
    private List<InstancePortBinding> getInstancePortBindings(final Task task, final NodeInfo node, final PortAllocator portAllocator) {
        final List<InstancePortBinding> portBindings = new ArrayList<>();
        for (final var pod : task.getPods()) {
            if (pod instanceof PodWithPorts) {
                final var portBindingMap = portAllocator.allocate(((PodWithPorts) pod).getPorts());
                for (final var entry : portBindingMap.entrySet()) {
                    final var portMapping = entry.getKey();
                    final var portBinding = entry.getValue();
                    final var internalPort = portMapping.getPort() + "/" + portMapping.getProtocol();
                    final var externalPort = portBinding.getExposedPort().toString();
                    portBindings.add(new InstancePortBinding(externalPort, internalPort, node.getPublicIP(), portMapping.isAdvertise()));
                }
            }
        }
        return portBindings;
    }

    @Override
    @ExcludeFromGeneratedTestReport
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(config.getReconciliationTickFrequency(), config.getReconciliationTickFrequency(), TimeUnit.MILLISECONDS);
    }
}
