package uk.co.ractf.polaris.node;

import com.github.dockerjava.api.model.AuthConfig;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.node.NodeInfo;

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
    void setNodeInfo(final NodeInfo nodeInfo);

    /**
     * Restarts an {@link Instance}
     *
     * @param instance the instance
     */
    void restartInstance(final Instance instance);

    /**
     * Gets the docker registry auth config for the host.
     *
     * @return the auth config
     */
    AuthConfig getAuthConfig();

}
