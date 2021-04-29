package uk.co.ractf.polaris.apiclient.command;

import com.fasterxml.jackson.core.type.TypeReference;
import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;
import uk.co.ractf.polaris.apiclient.AbstractCommand;

import java.util.Map;

public class ListChallengesCommand extends AbstractCommand<Map<String, Challenge>> {

    private String idFilter;

    public ListChallengesCommand(final APIClientTransport apiClientTransport) {
        super(apiClientTransport);
    }

    public ListChallengesCommand withIdFilter(final String idFilter) {
        this.idFilter = idFilter;
        return this;
    }

    @Override
    public Map<String, Challenge> exec() {
        String route = "/challenges";
        if (idFilter != null) {
            route += "?filter=" + idFilter;
        }

        return apiClientTransport.get(route, new TypeReference<>() {});
    }
}
