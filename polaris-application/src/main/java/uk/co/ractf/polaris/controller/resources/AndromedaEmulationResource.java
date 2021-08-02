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
import uk.co.ractf.polaris.api.instanceallocation.InstanceRequest;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.pod.PortMapping;
import uk.co.ractf.polaris.api.pod.ResourceQuota;
import uk.co.ractf.polaris.api.registry.credentials.ContainerRegistryCredentials;
import uk.co.ractf.polaris.api.registry.credentials.StandardRegistryCredentials;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.security.PolarisSecurityContext;
import uk.co.ractf.polaris.state.ClusterState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

@Path("/andromeda")
@Produces(MediaType.APPLICATION_JSON)
@Deprecated
public class AndromedaEmulationResource extends SecureResource {

    private final Controller controller;
    private final ClusterState clusterState;

    @Inject
    public AndromedaEmulationResource(final Controller controller, final ClusterState clusterState) {
        this.controller = controller;
        this.clusterState = clusterState;
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

        final ContainerRegistryCredentials credentials;

        if (challenge.getRegistryAuth().getUsername().equalsIgnoreCase("polaris")) {
            credentials = clusterState.getCredential(new NamespacedId(namespace, challenge.getRegistryAuth().getPassword()));
        } else {
            credentials = new StandardRegistryCredentials(new NamespacedId(namespace, challenge.getName()),
                    "standard",
                    new AuthConfig()
                            .withUsername(challenge.getRegistryAuth().getUsername())
                            .withPassword(challenge.getRegistryAuth().getPassword()));

            clusterState.setCredential(credentials);
        }

        final var resourceQuota = new ResourceQuota((long) challenge.getResources().getMemory(), 0L,
                (long) (Double.parseDouble(challenge.getResources().getCpus()) * 1_000_000_000));
        final var portMapping = new PortMapping(challenge.getPort(), "tcp", true);

        final var pod = new Container("container", challenge.getName(), challenge.getImage(), "",
                credentials.getId(), new ArrayList<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), resourceQuota, "always", new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), 5, Collections.singletonList(portMapping), new HashMap<>(), true);

        final var polarisChallenge = new Challenge(new NamespacedId(namespace, challenge.getName()), 0,
                Collections.singletonList(pod), new StaticReplication("static", challenge.getReplicas()),
                new Allocation("user", Integer.MAX_VALUE, Integer.MAX_VALUE));

        clusterState.setTask(polarisChallenge);
        return Response.status(200).entity(new AndromedaChallengeSubmitResponse(challenge.getName())).build();
    }

    @POST
    @Timed
    @ExceptionMetered
    @RolesAllowed("ANDROMEDA")
    @Operation(summary = "Request Instance Allocation", tags = {"Andromeda"},
            description = "Requests an instance allocation from polaris in andromda's format")
    public Response getInstance(@RequestBody final AndromedaInstanceRequest request) {
        if (clusterState.getTask(new NamespacedId(request.getJob())) == null) {
            return Response.status(404).build();
        }
        final var instance = controller.getInstanceAllocator().allocate(
                new InstanceRequest(new NamespacedId(request.getJob()), request.getUser(), ""));
        return Response.status(200).entity(
                new AndromedaInstance(clusterState.getNode(instance.getNodeId()).getPublicIP(),
                        Integer.parseInt(instance.getPortBindings().get(0).getPort().split("/")[0]))).build();
    }

    @POST
    @Path("/reset")
    @Timed
    @ExceptionMetered
    @RolesAllowed("ANDROMEDA")
    @Operation(summary = "Request Instance Reset", tags = {"Andromeda"},
            description = "Reset an instance allocation from polaris in andromda's format")
    public AndromedaInstance resetInstance(@RequestBody final AndromedaInstanceRequest request) {
        if (clusterState.getTask(new NamespacedId(request.getJob())) == null) {
            Response.status(404).build();
        }
        final var instance = controller.getInstanceAllocator().requestNewAllocation(
                new InstanceRequest(new NamespacedId(request.getJob()), request.getUser(), ""));
        return new AndromedaInstance(clusterState.getNode(instance.getNodeId()).getPublicIP(),
                Integer.parseInt(instance.getPortBindings().get(0).getPort().split("/")[0]));
    }

}
