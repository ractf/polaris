package uk.co.ractf.polaris.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.controller.Controller;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Resource providing API endpoints for {@link Challenge} objects.
 *
 * Roles defined: CHALLENGE_GET, CHALLENGE_SUBMIT, CHALLENGE_DELETE
 */
@Path("/challenges")
@Produces(MediaType.APPLICATION_JSON)
public class ChallengeResource {

    private static final Logger log = LoggerFactory.getLogger(ChallengeResource.class);

    private final Controller controller;

    public ChallengeResource(final Controller controller) {
        this.controller = controller;
    }

    /**
     * Return a {@link Map} of challenge id to {@link Challenge} for all challenges matching a given regex. Results are only
     * filtered if the {@code filter} parameter is specified and not empty.
     *
     * @param filter Regex to filter challenge ids by
     * @return A list of challenges matching the search
     */
    @GET
    @Timed
    @ExceptionMetered
    @RolesAllowed("CHALLENGE_GET")
    @Operation(summary = "Get Challenges", tags = {"Challenge"},
            description = "Gets a map of challenge id to challenge that matches a given regex.")
    public Map<String, Challenge> getChallenges(@QueryParam("filter") @DefaultValue("") final String filter) {
        final Map<String, Challenge> challengeMap = controller.getChallenges();
        if (filter.isEmpty()) {
            return challengeMap;
        }

        final Pattern pattern = Pattern.compile(filter);
        final Map<String, Challenge> filtered = new HashMap<>();
        for (Map.Entry<String, Challenge> challengeEntry : challengeMap.entrySet()) {
            if (pattern.matcher(challengeEntry.getKey()).find()) {
                filtered.put(challengeEntry.getKey(), challengeEntry.getValue());
            }
        }

        return filtered;
    }

    /**
     * Return a {@link Challenge} matching a given id.
     *
     * @param id id of the challenge
     * @return A challenge
     */
    @GET
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("CHALLENGE_GET")
    @Operation(summary = "Get Challenge By ID", tags = {"Challenge"}, description = "Get a challenge by id")
    public Challenge getChallenge(@PathParam("id") final String id) {
        return controller.getChallenge(id);
    }

    /**
     * Submit a {@link Challenge} object to the controller.
     *
     * @param challenge the challenge to submit
     */
    @POST
    @Timed
    @ExceptionMetered
    @RolesAllowed("CHALLENGE_SUBMIT")
    @Operation(summary = "Submit Challenge", tags = {"Challenge"},
            description = "Submits a challenge object to the controller")
    public void submitChallenge(final Challenge challenge) {
        controller.submitChallenge(challenge);
    }

    /**
     * Deletes a {@link Challenge} object matching a given id.
     *
     * @param id id of the challenge to delete
     */
    @DELETE
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("CHALLENGE_DELETE")
    @Operation(summary = "Delete Challenge", tags = {"Challenge"}, description = "Deletes a challenge from the controller")
    public void deleteChallenge(@PathParam("id") final String id) {
        controller.deleteChallenge(id);
    }

}
