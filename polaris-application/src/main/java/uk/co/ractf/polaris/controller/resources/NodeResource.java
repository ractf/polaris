package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import uk.co.ractf.polaris.api.node.NodeInfo;
import uk.co.ractf.polaris.node.Node;
import uk.co.ractf.polaris.state.ClusterState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Resource providing API endpoints for {@link NodeInfo} and {@link Node} objects.
 * <p>
 * Roles defined: HOST_GET
 */
@Path("/nodes")
@Produces(MediaType.APPLICATION_JSON)
public class NodeResource {

    private final ClusterState clusterState;

    @Inject
    public NodeResource(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    /**
     * Return a {@link Map} of host to {@link NodeInfo} for all hosts matching the host id regex. Results are only
     * filtered if a filter is not omitted.
     *
     * @param filter The regex to filter host ids by
     * @return the matching hosts
     */
    @GET
    @Timed
    @ExceptionMetered
    @RolesAllowed("ROOT")
    @Operation(summary = "Get Hosts", tags = {"Host"},
            description = "Gets a list of hosts currently registered and info about them")
    public Map<String, NodeInfo> listHostInfo(@QueryParam("filter") @DefaultValue("") final String filter) {
        final Map<String, NodeInfo> hostInfoList = new HashMap<>();
        final var pattern = Pattern.compile(filter);

        for (final var node : clusterState.getNodes().values()) {
            if (pattern.matcher(filter).find()) {
                hostInfoList.put(node.getId(), node);
            }
        }
        return hostInfoList;
    }

    /**
     * Gets {@link NodeInfo} from the host's id.
     *
     * @param id host id
     * @return host info
     */
    @GET
    @Timed
    @Path("/{id}")
    @ExceptionMetered
    @RolesAllowed("ROOT")
    @Operation(summary = "Get Host", tags = {"Host"},
            description = "Gets a host by id")
    public NodeInfo getHostInfo(@PathParam("id") final String id) {
        final var node = clusterState.getNode(id);
        if (node == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return node;
    }

}
