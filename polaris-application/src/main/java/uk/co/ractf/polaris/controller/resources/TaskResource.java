package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.api.task.*;
import uk.co.ractf.polaris.security.PolarisSecurityContext;
import uk.co.ractf.polaris.state.ClusterState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource extends SecureResource {

    private final ClusterState clusterState;

    @Inject
    public TaskResource(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @GET
    @Timed
    @ExceptionMetered
    @RolesAllowed("TASK_GET")
    @Operation(summary = "Get Tasks", tags = {"Task"},
            description = "Gets a map of task id to task within a given namespace which can be filtered by type or id regex.")
    public Map<NamespacedId, Task> getTasks(@Context final SecurityContext securityContext,
                                            @QueryParam("namespace") @DefaultValue("") final String namespace,
                                            @QueryParam("filter") @DefaultValue("") final String filter,
                                            @QueryParam("type") @DefaultValue("") final String type) {
        final var context = convertContext(securityContext);
        final Map<NamespacedId, Task> taskMap;
        if (namespace.isEmpty()) {
            if (context.isRoot()) {
                taskMap = clusterState.getTasks();
            } else {
                taskMap = new HashMap<>();
                for (final var namespaceId : context.getNamespaces()) {
                    taskMap.putAll(clusterState.getTasks(namespaceId));
                }
            }
        } else {
            if (!context.isUserInNamespace(namespace)) {
                throw new WebApplicationException(Response.Status.FORBIDDEN);
            }
            taskMap = clusterState.getTasks(namespace);
        }

        if (filter.isEmpty() && type.isEmpty()) {
            return taskMap;
        }

        final var pattern = Pattern.compile(filter);
        final var filtered = new HashMap<NamespacedId, Task>();
        for (final var entry : taskMap.entrySet()) {
            var matches = filter.isEmpty() || pattern.matcher(entry.getKey().getId()).find();
            matches &= type.isEmpty() || entry.getValue().getTaskType().name().equalsIgnoreCase(type);
            if (matches) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }

        return filtered;
    }

    @GET
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("TASK_GET")
    @Operation(summary = "Get Task", tags = {"Task"}, description = "Gets a task by given id")
    public Task getTask(@Context final SecurityContext securityContext, @PathParam("id") final NamespacedId id) {
        final var context = convertContext(securityContext);
        if (!context.isUserInNamespace(id.getNamespace())) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        final var task = clusterState.getTask(id);
        if (task == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return task;
    }

    @POST
    @Timed
    @ExceptionMetered
    @RolesAllowed("TASK_ADD")
    @Operation(summary = "Add Task", tags = {"Task"}, description = "Adds a task")
    public Response addTask(@Context final SecurityContext securityContext, @RequestBody final Task task) {
        final var context = convertContext(securityContext);
        if (!context.isUserInNamespace(task.getId().getNamespace())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new TaskSubmitResponse(TaskSubmitResponse.Status.FORBIDDEN_NAMESPACE, task.getId())).build();
        } else if (clusterState.getNamespace(task.getId().getNamespace()) == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new TaskSubmitResponse(TaskSubmitResponse.Status.BAD_NAMESPACE, task.getId())).build();
        }

        if (clusterState.getTask(task.getId()) != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new TaskSubmitResponse(TaskSubmitResponse.Status.DUPLICATE, task.getId())).build();
        }

        clusterState.setTask(task);
        return Response.status(Response.Status.CREATED)
                .entity(new TaskSubmitResponse(TaskSubmitResponse.Status.OK, task.getId())).build();
    }

    @PUT
    @Timed
    @ExceptionMetered
    @RolesAllowed("TASK_UPDATE")
    @Operation(summary = "Update Task", tags = {"Task"}, description = "Modifies a task")
    public Response updateTask(@Context final SecurityContext securityContext, @RequestBody final Task task) {
        final var context = convertContext(securityContext);
        if (!context.isUserInNamespace(task.getId().getNamespace())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new TaskUpdateResponse(TaskUpdateResponse.Status.FORBIDDEN_NAMESPACE, task.getId())).build();
        }

        if (clusterState.getTask(task.getId()) == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new TaskUpdateResponse(TaskUpdateResponse.Status.NOT_FOUND, task.getId())).build();
        }

        clusterState.setTask(task);
        return Response.ok(new TaskUpdateResponse(TaskUpdateResponse.Status.OK, task.getId())).build();
    }

    @DELETE
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("TASK_DELETE")
    @Operation(summary = "Delete Task", tags = {"Task"}, description = "Deletes a task")
    public Response deleteTask(@Context final SecurityContext securityContext, @PathParam("id") final NamespacedId id) {
        final var context = convertContext(securityContext);
        if (!context.isUserInNamespace(id.getNamespace())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new TaskDeleteResponse(TaskDeleteResponse.Status.FORBIDDEN_NAMESPACE, id)).build();
        }

        if (clusterState.getTask(id) == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new TaskDeleteResponse(TaskDeleteResponse.Status.NOT_FOUND, id)).build();
        }

        clusterState.deleteTask(id);
        return Response.ok(new TaskDeleteResponse(TaskDeleteResponse.Status.OK, id)).build();
    }

}
