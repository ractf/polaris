package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.api.namespace.NamespaceCreateResponse;
import uk.co.ractf.polaris.api.namespace.NamespaceDeleteResponse;
import uk.co.ractf.polaris.api.namespace.NamespaceUpdateResponse;
import uk.co.ractf.polaris.state.ClusterState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/namespaces")
@Produces(MediaType.APPLICATION_JSON)
public class NamespaceResource {

    private final ClusterState clusterState;

    @Inject
    public NamespaceResource(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @GET
    @Timed
    @ExceptionMetered
    @RolesAllowed("NAMESPACE_ADMIN")
    @Operation(summary = "Get Namespaces", tags = {"Namespace"}, description = "Gets a map of namespace id to namespace")
    public Map<String, Namespace> getNamespaces() {
        return clusterState.getNamespaces();
    }

    @GET
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("NAMESPACE_ADMIN")
    @Operation(summary = "Get Namespace", tags = {"Namespace"}, description = "Gets a namespace by id")
    public Namespace getNamespace(@PathParam("id") final String id) {
        return clusterState.getNamespace(id);
    }

    @POST
    @Timed
    @ExceptionMetered
    @RolesAllowed("NAMESPACE_ADMIN")
    @Operation(summary = "Create Namespace", tags = {"Namespace"}, description = "Creates a namespace")
    public Response createNamespace(@RequestBody final Namespace namespace) {
        if (clusterState.getNamespace(namespace.getName()) != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new NamespaceCreateResponse(NamespaceCreateResponse.Status.DUPLICATE, namespace.getName()))
                    .build();
        }

        clusterState.setNamespace(namespace);
        return Response
                .status(Response.Status.CREATED)
                .entity(new NamespaceCreateResponse(NamespaceCreateResponse.Status.OK, namespace.getName()))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("NAMESPACE_ADMIN")
    @Operation(summary = "Update Namespace", tags = {"Namespace"}, description = "Updates a namespace")
    public Response updateNamespace(@PathParam("id") final String id, @RequestBody final Namespace namespace) {
        if (clusterState.getNamespace(namespace.getName()) == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new NamespaceUpdateResponse(NamespaceUpdateResponse.Status.NOT_FOUND, namespace.getName()))
                    .build();
        }

        clusterState.setNamespace(namespace);
        return Response
                .status(Response.Status.OK)
                .entity(new NamespaceUpdateResponse(NamespaceUpdateResponse.Status.OK, namespace.getName()))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("NAMESPACE_ADMIN")
    @Operation(summary = "Update Namespace", tags = {"Namespace"}, description = "Updates a namespace")
    public Response deleteNamespace(@PathParam("id") final String id) {
        if (clusterState.getNamespace(id) == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new NamespaceDeleteResponse(NamespaceDeleteResponse.Status.NOT_FOUND, id))
                    .build();
        }

        clusterState.deleteNamespace(id);
        return Response
                .status(Response.Status.OK)
                .entity(new NamespaceDeleteResponse(NamespaceDeleteResponse.Status.OK, id))
                .build();
    }

}
