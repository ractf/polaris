package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.state.ClusterState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Resource for controlling {@link Instance} objects.
 * <p>
 * Roles defined: INSTANCE_LIST
 */
@Path("/instances")
@Produces(MediaType.APPLICATION_JSON)
public class InstanceResource {

    private static final Logger log = LoggerFactory.getLogger(InstanceResource.class);

    private final ClusterState clusterState;

    @Inject
    public InstanceResource(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    /**
     * Return a {@link Map} of instance id to {@link Instance} for all instances matching the host id regex
     * and the challenge id regex. Results are only filtered if a filter is not omitted.
     *
     * @param hostFilter      regex to apply to host ids
     * @param challengeFilter regex to apply to challenge ids
     * @return map of instance id to instance
     */
    @GET
    @Timed
    @ExceptionMetered
    @RolesAllowed("INSTANCE_LIST")
    @Operation(summary = "List Instances", tags = {"Instances"},
            description = "Get a map of instance id to instance which can be filtered by challenge id and host id")
    public Map<String, Instance> getInstances(
            @QueryParam("hostfilter") @DefaultValue("") final String hostFilter,
            @QueryParam("challengefilter") @DefaultValue("") final String challengeFilter) {
        final var hosts = clusterState.getNodes();
        final Map<String, Instance> instances = new HashMap<>();

        final var challengePattern = Pattern.compile(challengeFilter);
        final var hostPattern = Pattern.compile(hostFilter);

        for (final var hostEntry : hosts.entrySet()) {
            if (hostPattern.matcher(hostEntry.getKey()).find()) {
                final var hostInstances = clusterState.getInstancesOnNode(hostEntry.getKey());
                for (final var entry : hostInstances.entrySet()) {
                    if (challengePattern.matcher(entry.getValue().getTaskId().toString()).find()) {
                        instances.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        return instances;
    }


    @DELETE
    @Timed
    @ExceptionMetered
    @RolesAllowed("INSTANCE_DELETE")
    @Operation(summary = "Delete Instance", tags = {"Instances"},
            description = "Delete and deschedule an instance")
    public void deleteInstance(final String instanceId) {
        clusterState.deleteInstance(clusterState.getInstance(instanceId));
    }

}
