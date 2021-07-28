package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.api.notification.NotificationReceiver;
import uk.co.ractf.polaris.api.notification.NotificationReceiverCreateResponse;
import uk.co.ractf.polaris.api.notification.NotificationReceiverDeleteResponse;
import uk.co.ractf.polaris.api.notification.NotificationReceiverUpdateResponse;
import uk.co.ractf.polaris.state.ClusterState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.HashMap;
import java.util.Map;

@Path("/notification")
@Produces(MediaType.APPLICATION_JSON)
public class NotificationResource extends SecureResource {

    private final ClusterState clusterState;

    @Inject
    public NotificationResource(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @POST
    @Timed
    @ExceptionMetered
    @RolesAllowed("NOTIFICATION_ADD")
    @Operation(summary = "Add Notification Receiver", tags = {"Notification"}, 
            description = "Adds a notification receiver")
    public Response addNotificationReceiver(@Context final SecurityContext securityContext,
                                            @RequestBody final NotificationReceiver receiver) {
        final var context = convertContext(securityContext);
        if (!context.isUserInNamespace(receiver.getId().getNamespace())) {
            return Response.status(403)
                    .entity(new NotificationReceiverCreateResponse(
                            NotificationReceiverCreateResponse.Status.FORBIDDEN_NAMESPACE, receiver.getId().toString()))
                    .build();
        }

        if (!context.isRoot() && (receiver.isGlobal() || receiver.isSystem() || 
                !context.isUserInNamespace(receiver.getId().getNamespace()))) {
            return Response.status(403)
                    .entity(new NotificationReceiverCreateResponse(
                            NotificationReceiverCreateResponse.Status.TOO_BROAD, receiver.getId().toString()))
                    .build();
        }

        if (clusterState.getNotificationReceiver(receiver.getId()) != null) {
            return Response.status(400)
                    .entity(new NotificationReceiverCreateResponse(
                            NotificationReceiverCreateResponse.Status.DUPLICATE, receiver.getId().toString()))
                    .build();
        }

        clusterState.setNotificationReceiver(receiver);
        return Response.status(200)
                .entity(new NotificationReceiverCreateResponse(
                        NotificationReceiverCreateResponse.Status.OK, receiver.getId().toString()))
                .build();
    }

    @GET
    @Timed
    @ExceptionMetered
    @RolesAllowed("NOTIFICATION_GET")
    @Operation(summary = "Get Notification Receivers", tags = {"Notification"},
            description = "Gets a list of notification receivers with an optional namespace specifier")
    public Response getNotificationReceivers(@Context final SecurityContext securityContext,
                                             @QueryParam("filter") @DefaultValue("") final String namespace) {
        final var context = convertContext(securityContext);
        if (!namespace.equals("") && !context.isUserInNamespace(namespace)) {
            return Response.status(403).build();
        }

        Map<NamespacedId, NotificationReceiver> receivers;
        if (namespace.equals("")) {
            if (context.isRoot()) {
                return Response.ok(clusterState.getNotificationReceivers()).build();
            }
            receivers = new HashMap<>();
            for (final var allowedNamespace : context.getNamespaces()) {
                receivers.putAll(clusterState.getNotificationReceivers(allowedNamespace));
            }
        } else {
            receivers = clusterState.getNotificationReceivers(namespace);
        }

        return Response.ok(receivers).build();
    }

    @GET
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("NOTIFICATION_GET")
    @Operation(summary = "Get Notification Receiver", tags = {"Notification"},
            description = "Gets a notification receiver by id")
    public Response getNotificationReceiver(@Context final SecurityContext securityContext,
                                            @PathParam("id") final NamespacedId id) {
        final var context = convertContext(securityContext);
        if (!context.isRoot() && !context.isUserInNamespace(id.getNamespace())) {
            return Response.status(403).build();
        }
        
        return Response.ok(clusterState.getNotificationReceiver(id)).build();
    }

    @PUT
    @Timed
    @ExceptionMetered
    @RolesAllowed("NOTIFICATION_UPDATE")
    @Operation(summary = "Update Notification Receiver", tags = {"Notification"},
            description = "Updates a notification receiver")
    public Response updateNotificationReceiver(@Context final SecurityContext securityContext,
                                            @RequestBody final NotificationReceiver receiver) {
        final var context = convertContext(securityContext);
        if (!context.isUserInNamespace(receiver.getId().getNamespace())) {
            return Response.status(403)
                    .entity(new NotificationReceiverUpdateResponse(
                            NotificationReceiverUpdateResponse.Status.FORBIDDEN_NAMESPACE, receiver.getId().toString()))
                    .build();
        }

        if (!context.isRoot() && (receiver.isGlobal() || receiver.isSystem() ||
                !context.isUserInNamespace(receiver.getId().getNamespace()))) {
            return Response.status(403)
                    .entity(new NotificationReceiverUpdateResponse(
                            NotificationReceiverUpdateResponse.Status.TOO_BROAD, receiver.getId().toString()))
                    .build();
        }

        if (clusterState.getNotificationReceiver(receiver.getId()) == null) {
            return Response.status(400)
                    .entity(new NotificationReceiverUpdateResponse(
                            NotificationReceiverUpdateResponse.Status.NOT_FOUND, receiver.getId().toString()))
                    .build();
        }

        clusterState.setNotificationReceiver(receiver);
        return Response.status(200)
                .entity(new NotificationReceiverUpdateResponse(
                        NotificationReceiverUpdateResponse.Status.OK, receiver.getId().toString()))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("NOTIFICATION_DELETE")
    @Operation(summary = "Delete Notification Receiver", tags = {"Notification"},
            description = "Deletes a notification receiver")
    public Response deleteNotificationReceiver(@Context final SecurityContext securityContext,
                                               @PathParam("id") final NamespacedId id) {
        final var context = convertContext(securityContext);
        if (!context.isUserInNamespace(id.getNamespace())) {
            return Response.status(403)
                    .entity(new NotificationReceiverDeleteResponse(
                            NotificationReceiverDeleteResponse.Status.FORBIDDEN_NAMESPACE, id.getNamespace()))
                    .build();
        }

        if (clusterState.getNotificationReceiver(id) == null) {
            return Response.status(400)
                    .entity(new NotificationReceiverDeleteResponse(
                            NotificationReceiverDeleteResponse.Status.NOT_FOUND, id.toString()))
                    .build();
        }

        clusterState.deleteNotificationReceiver(id);
        return Response.status(200)
                .entity(new NotificationReceiverDeleteResponse(
                        NotificationReceiverDeleteResponse.Status.OK, id.toString()))
                .build();
    }
    
}
