package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.github.dockerjava.api.model.AuthConfig;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import uk.co.ractf.polaris.api.andromeda.AndromedaChallenge;
import uk.co.ractf.polaris.api.andromeda.AndromedaChallengeSubmitResponse;
import uk.co.ractf.polaris.api.andromeda.AndromedaInstance;
import uk.co.ractf.polaris.api.andromeda.AndromedaInstanceRequest;
import uk.co.ractf.polaris.api.deployment.Allocation;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instanceallocation.InstanceRequest;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.api.notification.NotificationTarget;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.pod.PortMapping;
import uk.co.ractf.polaris.api.pod.ResourceQuota;
import uk.co.ractf.polaris.api.registry.credentials.ContainerRegistryCredentials;
import uk.co.ractf.polaris.api.registry.credentials.StandardRegistryCredentials;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.task.Task;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.controller.scheduler.Scheduler;
import uk.co.ractf.polaris.notification.NotificationFacade;
import uk.co.ractf.polaris.state.ClusterState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Path("/andromeda")
@Produces(MediaType.APPLICATION_JSON)
@Deprecated
public class AndromedaEmulationResource extends SecureResource {

    private final Controller controller;
    private final ClusterState clusterState;
    private final NotificationFacade notification;
    private final Scheduler scheduler;
    private final Map<String, Instance> singleUserInstances = new ConcurrentHashMap<>();

    @Inject
    public AndromedaEmulationResource(final Controller controller, final ClusterState clusterState,
                                      final NotificationFacade notification, final Scheduler scheduler) {
        this.controller = controller;
        this.clusterState = clusterState;
        this.notification = notification;
        this.scheduler = scheduler;
    }

    @POST
    @Path("/job/submit")
    @Timed
    @ExceptionMetered
    @RolesAllowed("ANDROMEDA")
    @Operation(summary = "Submit Andromeda Challenge", tags = {"Andromeda"},
            description = "Submits a challenges in a format compatible with Andromeda, to be converted to a Polaris challenge and deployment")
    public Response submitChallenge(@Context final SecurityContext securityContext,
                                    @RequestBody final AndromedaChallenge challenge) {
        final var context = convertContext(securityContext);
        final var namespace = context.isRoot() ? "polaris" : context.getNamespaces().get(0);
        final var name = UUID.randomUUID().toString();

        final ContainerRegistryCredentials credentials;

        if (challenge.getRegistryAuth().getUsername().equalsIgnoreCase("polaris")) {
            credentials = clusterState.getCredential(new NamespacedId(namespace, challenge.getRegistryAuth().getPassword()));
        } else {
            credentials = new StandardRegistryCredentials(new NamespacedId(namespace, name),
                    "standard",
                    new AuthConfig()
                            .withUsername(challenge.getRegistryAuth().getUsername())
                            .withPassword(challenge.getRegistryAuth().getPassword()));

            clusterState.setCredential(credentials);
        }

        if (challenge.getPort() == null) {
            notification.error(NotificationTarget.NAMESPACE_ADMIN, clusterState.getNamespace(namespace),
                    "Invalid challenge spec", challenge.toJsonString());
            return Response.status(400).build();
        }

        final var resourceQuota = new ResourceQuota((long) challenge.getResources().getMemory(), 0L,
                (long) (Double.parseDouble(challenge.getResources().getCpus()) * 1_000_000_000));
        final var portMapping = new PortMapping(challenge.getPort(), "tcp", true);

        final var pod = new Container("container", name, challenge.getImage(), "",
                credentials.getId(), new ArrayList<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), resourceQuota, "always", new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), 5, Collections.singletonList(portMapping), new HashMap<>(), true);

        final var polarisChallenge = new Challenge(new NamespacedId(namespace, name), 0,
                Collections.singletonList(pod), new StaticReplication("static", challenge.getReplicas()),
                new Allocation("user", Integer.MAX_VALUE, Integer.MAX_VALUE, false));

        clusterState.setTask(polarisChallenge);
        return Response.status(200).entity(new AndromedaChallengeSubmitResponse(name)).build();
    }

    private List<String> getExtra(final Instance instance) {
        final var extra = new ArrayList<String>();
        if (instance.getRandomEnv().isEmpty()) {
            return extra;
        }

        for (final var pod : clusterState.getTask(instance.getTaskId()).getPods()) {
            if (pod instanceof Container) {
                final var container = (Container) pod;
                for (final var entry : container.getRandomEnv().entrySet()) {
                    if (entry.getValue().getDisplay() != null) {
                        extra.add(entry.getValue().getDisplay().replace("{}", instance.getRandomEnv().get(entry.getKey())));
                    }
                }
            }
        }

        return extra;
    }

    private Response makeResponse(final Instance instance) {
        return Response.status(200).entity(
                new AndromedaInstance(clusterState.getNode(instance.getNodeId()).getPublicIP(),
                        Integer.parseInt(instance.getPortBindings().get(0).getPort().split("/")[0]),
                        getExtra(instance))).build();
    }

    private Instance getSingleUserInstance(final String user, final Task task) {
        if (singleUserInstances.containsKey(user)) {
            final var currentInstance = singleUserInstances.get(user);
            if (currentInstance.getTaskId().getId().equals(task.getId().getId())) {
                return currentInstance;
            }

            singleUserInstances.remove(user);
            clusterState.deleteInstance(currentInstance);
        }
        final var instance = scheduler.schedule(task);
        singleUserInstances.put(user, instance);
        return instance;
    }

    @POST
    @Timed
    @ExceptionMetered
    @RolesAllowed("ANDROMEDA")
    @Operation(summary = "Request Instance Allocation", tags = {"Andromeda"},
            description = "Requests an instance allocation from polaris in andromda's format")
    public Response getInstance(@Context final SecurityContext securityContext,
                                @RequestBody final AndromedaInstanceRequest request) {
        final var context = convertContext(securityContext);
        final var namespace = context.isRoot() ? "polaris" : context.getNamespaces().get(0);
        final var task = clusterState.getTask(new NamespacedId(namespace, request.getJob()));
        if (task == null) {
            return Response.status(404).build();
        }
        final var challenge = (Challenge) task;
        final Instance instance;
        if (challenge.getAllocation().isSingleUser()) {
            instance = getSingleUserInstance(request.getUser(), task);
        } else {
            instance = controller.getInstanceAllocator().allocate(
                    new InstanceRequest(new NamespacedId(namespace, request.getJob()), request.getUser(), ""));
        }
        return makeResponse(instance);
    }

    @POST
    @Path("/reset")
    @Timed
    @ExceptionMetered
    @RolesAllowed("ANDROMEDA")
    @Operation(summary = "Request Instance Reset", tags = {"Andromeda"},
            description = "Reset an instance allocation from polaris in andromda's format")
    public Response resetInstance(@Context final SecurityContext securityContext,
                                           @RequestBody final AndromedaInstanceRequest request) {
        final var context = convertContext(securityContext);
        final var namespace = context.isRoot() ? "polaris" : context.getNamespaces().get(0);
        final var task = clusterState.getTask(new NamespacedId(namespace, request.getJob()));
        if (task == null) {
            return Response.status(404).build();
        }

        final var currentSingleUserInstance = singleUserInstances.get(request.getUser());
        singleUserInstances.remove(request.getUser());
        clusterState.deleteInstance(currentSingleUserInstance);
        final var challenge = (Challenge) task;
        final Instance instance;
        if (challenge.getAllocation().isSingleUser()) {
            instance = getSingleUserInstance(request.getUser(), task);
        } else {
            instance = controller.getInstanceAllocator().requestNewAllocation(
                    new InstanceRequest(new NamespacedId(namespace, request.getJob()), request.getUser(), ""));
        }
        return makeResponse(instance);
    }

}
