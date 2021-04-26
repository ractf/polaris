package uk.co.ractf.polaris.node;

import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.api.pod.PortMapping;
import uk.co.ractf.polaris.node.runner.Runner;
import uk.co.ractf.polaris.node.service.NodeServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class ConsulNode implements Node, Managed {

    private static final Logger log = LoggerFactory.getLogger(ConsulNode.class);

    private final String id;
    private final Set<Runner<?>> runnerSet;
    private final Set<Service> services;
    private final Map<Class<? extends Pod>, Runner<? extends Pod>> runners = new HashMap<>();

    @Inject
    public ConsulNode(final NodeConfiguration configuration,
                      final Set<Runner<?>> runnerSet,
                      @NodeServices final Set<Service> services) {
        this.id = "node"; //TODO
        this.runnerSet = runnerSet;
        this.services = services;
        log.info("consul node start");
    }

    @Override
    public void start() {
        for (final Runner<?> runner : runnerSet) {
            this.runners.put(runner.getType(), runner);
        }
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
    public String getId() {
        return id;
    }

    @Override
    public NodeInfo getHostInfo() {
        return null;
    }

    @Override
    public void setHostInfo(final NodeInfo nodeInfo) {

    }

    @Override
    public void restartInstance(final Instance instance) {

    }

    @Override
    public Map<PortMapping, PortBinding> createPortBindings(final List<PortMapping> portMappings) {
        return null;
    }

    @Override
    public AuthConfig getAuthConfig() {
        return null;
    }
}
