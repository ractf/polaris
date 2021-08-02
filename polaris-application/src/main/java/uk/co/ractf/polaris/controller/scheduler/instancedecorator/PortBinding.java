package uk.co.ractf.polaris.controller.scheduler.instancedecorator;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instance.InstancePortBinding;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.pod.PodWithPorts;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.controller.PortAllocator;
import uk.co.ractf.polaris.controller.scheduler.InstanceDecoratorPlugin;

import java.util.ArrayList;
import java.util.List;

public class PortBinding implements InstanceDecoratorPlugin {

    private final ControllerConfiguration config;

    @Inject
    public PortBinding(final ControllerConfiguration config) {
        this.config = config;
    }

    @Override
    public Instance decorate(final Instance instance, final Task task, final NodeInfo node) {
        final var portAllocator = new PortAllocator(config.getMinPort(), config.getMaxPort(), node.getPortAllocations());
        final var portBindings = getInstancePortBindings(task, node, portAllocator);
        for (final var portBinding : portBindings) {
            instance.addPortBinding(portBinding);
        }
        return instance;
    }

    @Override
    public String getName() {
        return "PortBinding";
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
                    final var internalPort = portBinding.getExposedPort().toString();
                    final var externalPort = portBinding.getBinding().getHostPortSpec();
                    portBindings.add(new InstancePortBinding(externalPort, internalPort, node.getPublicIP(), portMapping.isAdvertise()));
                }
            }
        }
        return portBindings;
    }

}
