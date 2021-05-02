package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.deployment.Allocation;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instanceallocation.InstanceRequest;
import uk.co.ractf.polaris.api.instanceallocation.InstanceResponse;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.controller.instanceallocation.InstanceAllocator;
import uk.co.ractf.polaris.state.ClusterState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource to control allocation of instances to a user or team
 * <p>
 * Roles defined: ALLOCATE_INSTANCE
 */
@Path("/instanceallocation")
@Produces(MediaType.APPLICATION_JSON)
public class InstanceAllocationResource {

    private static final Logger log = LoggerFactory.getLogger(InstanceAllocationResource.class);

    private final Controller controller;
    private final ClusterState clusterState;

    @Inject
    public InstanceAllocationResource(final Controller controller, final ClusterState clusterState) {
        this.controller = controller;
        this.clusterState = clusterState;
    }

    /**
     * Gets an {@link Instance} for a user for a given challenge, attempting to comply with the {@link Allocation}
     * restraints specified in the {@link uk.co.ractf.polaris.api.task.Challenge} config, if the user or a user on the
     * same team has already requested a challenge, they may be routed back to the same instance.
     *
     * @param instanceRequest the details of the request for instance
     * @return the instance and host ip
     */
    @POST
    @Timed
    @ExceptionMetered
    @RolesAllowed("ALLOCATE_INSTANCE")
    @Operation(summary = "Allocate Instance", tags = {"Instance Allocation"},
            description = "Allocating an instance to the user, if the user/team has already been allocated an instance, they may be given the same one based on allocation rules.")
    public InstanceResponse getInstance(final InstanceRequest instanceRequest) {
        final var task = clusterState.getTask(instanceRequest.getTaskId());
        if (!(task instanceof Challenge)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final var instance = controller.getInstanceAllocator().allocate(instanceRequest);
        return createInstanceResponse(instance);
    }

    /**
     * Gets an {@link Instance} for a user for a given challenge that is different from their last one, and avoiding
     * the other instances they have been given previously. The user will no longer be served this instance unless
     * the {@link InstanceAllocator} is unable to allocate an instance within
     * the usual constraints
     *
     * @param instanceRequest the instance to replace
     * @return a new instance
     */
    @POST
    @Path("/new")
    @Timed
    @ExceptionMetered
    @RolesAllowed("ALLOCATE_INSTANCE")
    @Operation(summary = "Request New Instance Allocation", tags = {"Instance Allocation"},
            description = "Get an instance allocation that is guaranteed to be different from the last one")
    public InstanceResponse newInstance(final InstanceRequest instanceRequest) {
        final var task = clusterState.getTask(instanceRequest.getTaskId());
        if (!(task instanceof Challenge)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final var instance = controller.getInstanceAllocator().requestNewAllocation(instanceRequest);
        return createInstanceResponse(instance);
    }

    @NotNull
    private InstanceResponse createInstanceResponse(final Instance instance) {
        if (instance == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        final var node = clusterState.getNode(instance.getNodeId());
        if (node == null) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return new InstanceResponse(node.getPublicIP(), instance);
    }

}
