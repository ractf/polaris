package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import uk.co.ractf.polaris.security.PolarisSecurityContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Map;

@Path("/ping")
@Produces(MediaType.APPLICATION_JSON)
public class PingResource {

    @GET
    @Timed
    @ExceptionMetered
    @Operation(summary = "Ping", tags = {"Ping"}, description = "Ping")
    public Map<String, String> ping(@Context final PolarisSecurityContext context) {
        if (context.getUserPrincipal() == null) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        return Map.of("username", context.getUserPrincipal().getName());
    }

}
