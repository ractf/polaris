package uk.co.ractf.polaris.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.api.instanceallocation.InstanceRequest;
import uk.co.ractf.polaris.api.instanceallocation.InstanceResponse;
import uk.co.ractf.polaris.controller.Controller;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/instanceallocation")
@Produces(MediaType.APPLICATION_JSON)
public class InstanceAllocationResource {

    private static final Logger log = LoggerFactory.getLogger(InstanceAllocationResource.class);

    private final Controller controller;

    public InstanceAllocationResource(final Controller controller) {
        this.controller = controller;
    }

    @POST
    @Timed
    @ExceptionMetered
    public InstanceResponse getInstance(final InstanceRequest instanceRequest) {
        final Instance instance = controller.getInstanceAllocator().allocate(instanceRequest);
        if (instance == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return new InstanceResponse(controller.getHost(instance.getHostID()).getPublicIp(), instance);
    }

    @POST
    @Path("/new")
    @Timed
    @ExceptionMetered
    public InstanceResponse newInstance(final InstanceRequest instanceRequest) {
        final Instance instance = controller.getInstanceAllocator().reset(instanceRequest);
        if (instance == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return new InstanceResponse(controller.getHost(instance.getHostID()).getPublicIp(), instance);
    }

}
