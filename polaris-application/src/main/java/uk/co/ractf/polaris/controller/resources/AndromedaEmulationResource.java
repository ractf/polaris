package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import uk.co.ractf.polaris.api.andromeda.AndromedaChallenge;
import uk.co.ractf.polaris.api.andromeda.AndromedaChallengeSubmitResponse;
import uk.co.ractf.polaris.api.andromeda.AndromedaInstance;
import uk.co.ractf.polaris.api.andromeda.AndromedaInstanceRequest;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Allocation;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.deployment.StaticReplication;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instanceallocation.InstanceRequest;
import uk.co.ractf.polaris.api.pod.Container;
import uk.co.ractf.polaris.api.pod.Pod;
import uk.co.ractf.polaris.api.pod.PortMapping;
import uk.co.ractf.polaris.api.pod.ResourceQuota;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.controller.ControllerConfiguration;
import uk.co.ractf.polaris.state.ClusterState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

@Path("/andromeda")
@Produces(MediaType.APPLICATION_JSON)
@Deprecated
public class AndromedaEmulationResource {

    private final Controller controller;
    private final ClusterState clusterState;
    private final ControllerConfiguration configuration;

    @Inject
    public AndromedaEmulationResource(final Controller controller, final ClusterState clusterState, final ControllerConfiguration configuration) {
        this.controller = controller;
        this.clusterState = clusterState;
        this.configuration = configuration;
    }

    @POST
    @Path("/job/submit")
    @Timed
    @ExceptionMetered
    @RolesAllowed("ANDROMEDA_CHALLENGE_SUBMIT")
    @Operation(summary = "Submit Andromeda Challenge", tags = {"Andromeda"},
            description = "Submits a challenges in a format compatible with Andromeda, to be converted to a Polaris challenge and deployment")
    public Response submitChallenge(final AndromedaChallenge challenge) {
        final ResourceQuota resourceQuota = new ResourceQuota(
                (long) challenge.getResources().getMemory(),
                0L,
                (long) (Double.parseDouble(challenge.getResources().getCpus()) * 1_000_000_000));
        final PortMapping portMapping = new PortMapping(challenge.getPort(), "tcp", true);
        final Pod pod = new Container("container", challenge.getName(), challenge.getImage(), "",
                new ArrayList<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>(),
                new ArrayList<>(), resourceQuota, "always", new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), 5, Collections.singleton(portMapping), new HashMap<>());
        final Challenge polarisChallenge = new Challenge(challenge.getName(), Collections.singletonList(pod));
        //configuration.setRegistryUsername(challenge.getRegistryAuth().getUsername());
        //configuration.setRegistryPassword(challenge.getRegistryAuth().getPassword());
        controller.createChallenge(polarisChallenge);
        final Deployment deployment = new Deployment(challenge.getName(), challenge.getName(),
                new StaticReplication("static", challenge.getReplicas()),
                new Allocation("user", Integer.MAX_VALUE, Integer.MAX_VALUE));
        clusterState.setDeployment(deployment);
        return Response.status(200).entity(new AndromedaChallengeSubmitResponse(challenge.getName())).build();
    }

    @POST
    @Timed
    @ExceptionMetered
    @RolesAllowed("ANDROMEDA_GET_INSTANCE")
    @Operation(summary = "Request Instance Allocation", tags = {"Andromeda"},
            description = "Requests an instance allocation from polaris in andromda's format")
    public Response getInstance(final AndromedaInstanceRequest request) {
        if (clusterState.getChallenge(request.getJob()) == null) {
            return Response.status(404).build();
        }
        final Instance instance = controller.getInstanceAllocator().allocate(
                new InstanceRequest(request.getJob(), request.getUser(), ""));
        return Response.status(200).entity(
                new AndromedaInstance(clusterState.getNode(instance.getNodeId()).getPublicIP(),
                        Integer.parseInt(instance.getPortBindings().get(0).getPort().split("/")[0]))).build();
    }

    @POST
    @Path("/reset")
    @Timed
    @ExceptionMetered
    @RolesAllowed("ANDROMEDA_RESET_INSTANCE")
    @Operation(summary = "Request Instance Reset", tags = {"Andromeda"},
            description = "Reset an instance allocation from polaris in andromda's format")
    public AndromedaInstance resetInstance(final AndromedaInstanceRequest request) {
        if (clusterState.getChallenge(request.getJob()) == null) {
            Response.status(404).build();
        }
        final Instance instance = controller.getInstanceAllocator().requestNewAllocation(
                new InstanceRequest(request.getJob(), request.getUser(), ""));
        return new AndromedaInstance(clusterState.getNode(instance.getNodeId()).getPublicIP(),
                Integer.parseInt(instance.getPortBindings().get(0).getPort().split("/")[0]));
    }

}
