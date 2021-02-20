package uk.co.ractf.polaris.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.controller.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Path("/challenges")
@Produces(MediaType.APPLICATION_JSON)
public class ChallengeResource {

    private static final Logger log = LoggerFactory.getLogger(ChallengeResource.class);

    private final Controller controller;

    public ChallengeResource(final Controller controller) {
        this.controller = controller;
    }

    @GET
    @Timed
    @ExceptionMetered
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

    @GET
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    public Challenge getChallenge(@PathParam("id") final String id) {
        return controller.getChallenge(id);
    }

    @POST
    @Timed
    @ExceptionMetered
    public void createChallenge(final Challenge challenge) {
        controller.createChallenge(challenge);
    }

    @DELETE
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    public void deleteChallenge(@PathParam("id") final String id) {
        controller.deleteChallenge(id);
    }

}
