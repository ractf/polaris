package uk.co.ractf.polaris.host;

import com.github.dockerjava.api.model.PortBinding;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.host.HostInfo;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.pod.PortMapping;

import java.util.List;
import java.util.Map;

/**
 * The interface used for interacting with hosts
 */
public interface Host {

    /**
     * Get the host's id
     *
     * @return the host's id
     */
    String getID();

    /**
     * Gets a {@link HostInfo} object with the host's current state
     *
     * @return host info
     */
    HostInfo getHostInfo();

    /**
     * Sets the host's current state
     *
     * @param hostInfo {@link HostInfo}
     */
    void setHostInfo(final HostInfo hostInfo);

    /**
     * Creates an instance of a {@link Challenge} on this host with a certain {@link Deployment} group
     *
     * @param challenge the challenge
     * @param deployment the deployment of the challenge
     * @return an instance of the challenge
     */
    Instance createInstance(final Challenge challenge, final Deployment deployment);

    /**
     * Removes an {@link Instance} from a host
     *
     * @param instance the instance
     */
    void removeInstance(final Instance instance);

    /**
     * Get a {@link Map} of instance id to {@link Instance} for instances scheduled on this host.
     *
     * @return the instance map
     */
    Map<String, Instance> getInstances();

    /**
     * Restarts an {@link Instance}
     *
     * @param instance the instance
     */
    void restartInstance(final Instance instance);

    /**
     * Gets an {@link Instance} by id
     *
     * @param id the id of the instance
     * @return the instance
     */
    Instance getInstance(final String id);

    /**
     * Generates {@link PortBinding}s for all the {@link PortMapping}s provided based on which ports are available on
     * the host.
     *
     * @param portMappings the portmappings
     * @return the map of portmapping to portbinding
     */
    Map<PortMapping, PortBinding> createPortBindings(List<PortMapping> portMappings);

}
