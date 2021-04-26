package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import io.swagger.v3.oas.annotations.Operation;
import org.eclipse.jetty.server.Authentication;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/ping")
@Produces(MediaType.APPLICATION_JSON)
public class PingResource {

    @GET
    @Timed
    @ExceptionMetered
    @Operation(summary = "Ping", tags = {"Ping"},
            description = "Returns the current user's username")
    public Map<String, String> ping(@Auth final Authentication.User user) {
        return Map.of("username", user.getUserIdentity().getUserPrincipal().getName());
    }

}
