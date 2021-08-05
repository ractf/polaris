package uk.co.ractf.polaris.node.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import uk.co.ractf.polaris.controller.resources.SecureResource;
import uk.co.ractf.polaris.node.service.GarbageCollectionService;
import uk.co.ractf.polaris.node.service.OrphanKillerService;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource extends SecureResource {

    private final GarbageCollectionService gcService;
    private final OrphanKillerService orphanKillerService;

    @Inject
    public AdminResource(final GarbageCollectionService gcService, final OrphanKillerService orphanKillerService) {
        this.gcService = gcService;
        this.orphanKillerService = orphanKillerService;
    }

    @POST
    @Path("/gc")
    @Timed
    @ExceptionMetered
    @RolesAllowed("ROOT")
    @Operation(summary = "Garbage Collection", tags = {"Admin"}, description = "Force a node to garbage collect its pods.")
    public void garbageCollect() {
        gcService.runOneIteration();
    }

    @POST
    @Path("/killOrphans")
    @Timed
    @ExceptionMetered
    @RolesAllowed("ROOT")
    @Operation(summary = "Kill Orphaned Containers", tags = {"Admin"}, description = "Force a node to remove all pods that do not belong to a task.")
    public void killOrphans() {
        orphanKillerService.runOneIteration();
    }

}
