package uk.co.ractf.polaris.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.controller.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Path("/deployments")
@Produces(MediaType.APPLICATION_JSON)
public class DeploymentResource {

    private static final Logger log = LoggerFactory.getLogger(DeploymentResource.class);

    private final Controller controller;

    public DeploymentResource(final Controller controller) {
        this.controller = controller;
    }

    @GET
    @Timed
    @ExceptionMetered
    public Map<String, Deployment> getDeployments(@QueryParam("filter") @DefaultValue("") final String filter,
                                                  @QueryParam("challengefilter") @DefaultValue("") final String challengeFilter) {
        final Map<String, Deployment> deploymentMap = controller.getDeployments();
        if (filter.isEmpty() && challengeFilter.isEmpty()) {
            return deploymentMap;
        }

        final Pattern pattern = Pattern.compile(filter);
        final Pattern challengePattern = Pattern.compile(challengeFilter);
        final Map<String, Deployment> filtered = new HashMap<>();
        for (Map.Entry<String, Deployment> entry : deploymentMap.entrySet()) {
            if (pattern.matcher(entry.getKey()).find() && challengePattern.matcher(entry.getValue().getChallenge()).find()) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }

        return filtered;
    }

    @GET
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    public Deployment getDeployment(@PathParam("id") final String id) {
        return controller.getDeployment(id);
    }

    @POST
    @Timed
    @ExceptionMetered
    public void createDeployment(final Deployment deployment) {
        controller.createDeployment(deployment);
    }

    @PUT
    @Timed
    @ExceptionMetered
    public void updateDeployment(final Deployment deployment) {
        controller.updateDeployment(deployment);
    }

    @DELETE
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    public void deleteDeployment(@PathParam("id") final String deployment) {
        controller.deleteDeployment(deployment);
    }

}
