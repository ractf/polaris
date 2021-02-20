package uk.co.ractf.polaris.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ractf.polaris.api.instance.Instance;
import uk.co.ractf.polaris.controller.Controller;
import uk.co.ractf.polaris.host.Host;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Path("/instances")
@Produces(MediaType.APPLICATION_JSON)
public class InstanceResource {

    private static final Logger log = LoggerFactory.getLogger(InstanceResource.class);

    private final Controller controller;

    public InstanceResource(final Controller controller) {
        this.controller = controller;
    }

    @GET
    @Timed
    @ExceptionMetered
    public Map<String, Instance> getInstances(
            @QueryParam("hostfilter") @DefaultValue("") final String hostFilter,
            @QueryParam("challengefilter") @DefaultValue("") final String challengeFilter) {
        final Map<String, Host> hosts = controller.getHosts();
        final Map<String, Instance> instances = new HashMap<>();

        final Pattern challengePattern = Pattern.compile(challengeFilter);
        final Pattern hostPattern = Pattern.compile(hostFilter);

        for (final Map.Entry<String, Host> hostEntry : hosts.entrySet()) {
            if (hostPattern.matcher(hostEntry.getKey()).find()) {
                final Map<String, Instance> hostInstances = hostEntry.getValue().getInstances();
                for (final Map.Entry<String, Instance> entry : hostInstances.entrySet()) {
                    if (challengePattern.matcher(entry.getValue().getChallengeID()).find()) {
                        instances.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        return instances;
    }

}
