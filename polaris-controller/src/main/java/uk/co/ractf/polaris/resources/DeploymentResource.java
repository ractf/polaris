package uk.co.ractf.polaris.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.controller.Controller;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Resource providing API endpoints for {@link Deployment} objects
 * <p>
 * Roles defined: DEPLOYMENT_GET, DEPLOYMENT_CREATE, DEPLOYMENT_UPDATE, DEPLOYMENT_DELETE
 */
@Path("/deployments")
@Produces(MediaType.APPLICATION_JSON)
public class DeploymentResource {

    private static final Logger log = LoggerFactory.getLogger(DeploymentResource.class);

    private final Controller controller;

    @Inject
    public DeploymentResource(final Controller controller) {
        this.controller = controller;
    }

    /**
     * Return a {@link Map} of deployment id to {@link Deployment} for all deployments matching the deployment id regex
     * and the challenge id regex. Results are only filtered if a filter is not omitted.
     *
     * @param deploymentFilter regex to apply to deployment ids
     * @param challengeFilter  regex to apply to challenge ids
     * @return the matching deployments
     */
    @GET
    @Timed
    @ExceptionMetered
    @RolesAllowed("DEPLOYMENT_GET")
    @Operation(summary = "Get Deployments", tags = {"Deployment"},
            description = "Gets a map of deployment id to deployment that matches a given regex on id and challenge id.")
    public Map<String, Deployment> getDeployments(@QueryParam("filter") @DefaultValue("") final String deploymentFilter,
                                                  @QueryParam("challengefilter") @DefaultValue("") final String challengeFilter) {
        final Map<String, Deployment> deploymentMap = controller.getDeployments();
        if (deploymentFilter.isEmpty() && challengeFilter.isEmpty()) {
            return deploymentMap;
        }

        final Pattern pattern = Pattern.compile(deploymentFilter);
        final Pattern challengePattern = Pattern.compile(challengeFilter);
        final Map<String, Deployment> filtered = new HashMap<>();
        for (final Map.Entry<String, Deployment> entry : deploymentMap.entrySet()) {
            if (pattern.matcher(entry.getKey()).find() && challengePattern.matcher(entry.getValue().getChallenge()).find()) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }

        return filtered;
    }

    /**
     * Gets a {@link Deployment} that has a given id
     *
     * @param id id of the deployment
     * @return the deployment
     */
    @GET
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("DEPLOYMENT_GET")
    @Operation(summary = "Get Deployment", tags = {"Deployment"}, description = "Gets a deployment with a certain id")
    public Deployment getDeployment(@PathParam("id") final String id) {
        return controller.getDeployment(id);
    }

    /**
     * Creates a {@link Deployment} on the controller, to be rolled out eventually
     *
     * @param deployment the deployment
     */
    @POST
    @Timed
    @ExceptionMetered
    @RolesAllowed("DEPLOYMENT_CREATE")
    @Operation(summary = "Create Deployment", tags = {"Deployment"},
            description = "Creates a deployment on the controller which will roll it out eventually")
    public void createDeployment(final Deployment deployment) {
        controller.createDeployment(deployment);
    }

    /**
     * Send an update to a {@link Deployment} to the controller so changes can be applied to a live deployment.
     *
     * @param deployment the deployment to update
     */
    @PUT
    @Timed
    @ExceptionMetered
    @RolesAllowed("DEPLOYMENT_UPDATE")
    @Operation(summary = "Update Deployment", tags = {"Deployment"}, description = "Updates a given deployment")
    public void updateDeployment(final Deployment deployment) {
        controller.updateDeployment(deployment);
    }

    /**
     * Deletes a {@link Deployment} with a given id
     *
     * @param deployment the id of the deployment to delete
     */
    @DELETE
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("DEPLOYMENT_DELETE")
    @Operation(summary = "Delete Deployment", tags = {"Deployment"}, description = "Deletes a deployment with a certain id")
    public void deleteDeployment(@PathParam("id") final String deployment) {
        controller.deleteDeployment(deployment);
    }

}
