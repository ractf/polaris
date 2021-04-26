package uk.co.ractf.polaris.node;

import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.PortBinding;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.api.pod.PortMapping;

import java.util.List;
import java.util.Map;

/**
 * The interface used for interacting with hosts
 */
public interface Node {

    /**
     * Get the host's id
     *
     * @return the host's id
     */
    String getId();

    /**
     * Gets a {@link NodeInfo} object with the host's current state
     *
     * @return host info
     */
    NodeInfo getNodeInfo();

    /**
     * Sets the host's current state
     *
     * @param nodeInfo {@link NodeInfo}
     */
    void getNodeInfo(final NodeInfo nodeInfo);

    /**
     * Restarts an {@link Instance}
     *
     * @param instance the instance
     */
    void restartInstance(final Instance instance);

    /**
     * Generates {@link PortBinding}s for all the {@link PortMapping}s provided based on which ports are available on
     * the host.
     *
     * @param portMappings the portmappings
     * @return the map of portmapping to portbinding
     */
    Map<PortMapping, PortBinding> createPortBindings(List<PortMapping> portMappings);

    /**
     * Gets the docker registry auth config for the host.
     *
     * @return the auth config
     */
    AuthConfig getAuthConfig();

}
