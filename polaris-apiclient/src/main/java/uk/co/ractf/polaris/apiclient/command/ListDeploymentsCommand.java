package uk.co.ractf.polaris.apiclient.command;

import com.fasterxml.jackson.core.type.TypeReference;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

import java.util.Map;

public class ListDeploymentsCommand extends AbstractCommand<Map<String, Deployment>> {

    private String idFilter;
    private String challengeIdFilter;

    public ListDeploymentsCommand(final APIClientTransport apiClientTransport) {
        super(apiClientTransport);
    }

    public ListDeploymentsCommand withIdFilter(final String idFilter) {
        this.idFilter = idFilter;
        return this;
    }

    public ListDeploymentsCommand withChallengeIdFilter(final String challengeIdFilter) {
        this.challengeIdFilter = challengeIdFilter;
        return this;
    }

    @Override
    public Map<String, Deployment> exec() {
        String route = "/deployments";
        if (idFilter != null) {
            route += "?filter=" + idFilter;
            if (challengeIdFilter != null) {
                route += "&challengefilter=" + challengeIdFilter;
            }
        } else if (challengeIdFilter != null) {
            route += "?challengefilter=" + challengeIdFilter;
        }

        return apiClientTransport.get(route, new TypeReference<>() {});
    }
}
