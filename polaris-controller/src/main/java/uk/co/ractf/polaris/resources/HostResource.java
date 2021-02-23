package uk.co.ractf.polaris.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import uk.co.ractf.polaris.api.host.HostInfo;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.host.Host;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Path("/hosts")
@Produces(MediaType.APPLICATION_JSON)
public class HostResource {

    private final Controller controller;

    @Inject
    public HostResource(final Controller controller) {
        this.controller = controller;
    }

    @GET
    @Timed
    @ExceptionMetered
    @RolesAllowed("HOST_GET")
    @Operation(summary = "Get Deployments", tags = {"Deployment"},
            description = "Gets a map of deployment id to deployment that matches a given regex on id and challenge id.")
    public List<HostInfo> getHostInfo() {
        final List<HostInfo> hostInfoList = new ArrayList<>();
        for (final Host host : controller.getHosts().values()) {
            hostInfoList.add(host.getHostInfo());
        }
        return hostInfoList;
    }

}
