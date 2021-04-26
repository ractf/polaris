package uk.co.ractf.polaris.node;

import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.pod.PortMapping;

import java.util.List;
import java.util.Map;

@Singleton
public class ConsulNode implements Node {

    private final String id;

    @Inject
    public ConsulNode() {
        this.id = ""; //TODO
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
    public Instance createInstance(final Challenge challenge, final Deployment deployment) {
        return null;
    }

    @Override
    public void removeInstance(final Instance instance) {

    }

    @Override
    public Map<String, Instance> getInstances() {
        return null;
    }

    @Override
    public void restartInstance(final Instance instance) {

    }

    @Override
    public Instance getInstance(final String id) {
        return null;
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
