package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import uk.co.ractf.polaris.api.authentication.APIToken;
import uk.co.ractf.polaris.state.ClusterState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/api_tokens")
@Produces(MediaType.APPLICATION_JSON)
public class APITokenResource {

    private final ClusterState clusterState;

    @Inject
    public APITokenResource(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @GET
    @Timed
    @ExceptionMetered
    @RolesAllowed("ROOT")
    @Operation(summary = "Get API Tokens", tags = {"Authentication"},
            description = "Gets a map of api token id to api token.")
    public Map<String, APIToken> getAPITokens() {
        return clusterState.getAPITokens();
    }

    @GET
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("ROOT")
    @Operation(summary = "Get API Token", tags = {"Authentication"},
            description = "Gets an api token by id.")
    public Response getAPITokens(@PathParam("id") final String id) {
        final var apiToken = clusterState.getAPIToken(id);
        if (apiToken != null) {
            return Response.ok(apiToken).build();
        }
        return Response.status(404).build();
    }

    @POST
    @Timed
    @ExceptionMetered
    @RolesAllowed("ROOT")
    @Operation(summary = "Add API Token", tags = {"Authentication"}, description = "Adds an api token.")
    public Response createAPIToken(@RequestBody final APIToken apiToken) {
        clusterState.setAPIToken(apiToken);
        return Response.ok(apiToken).build();
    }

    @DELETE
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("ROOT")
    @Operation(summary = "Delete API Token", tags = {"Authentication"}, description = "Deletes an api token.")
    public Response deleteAPIToken(@PathParam("id") final String id) {
        clusterState.deleteAPIToken(id);
        return Response.ok().build();
    }

}
