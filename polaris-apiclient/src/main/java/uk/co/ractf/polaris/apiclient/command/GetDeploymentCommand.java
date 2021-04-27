package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class GetDeploymentCommand extends AbstractCommand<Deployment> {

    private final String id;

    public GetDeploymentCommand(final APIClientTransport apiClientTransport, final String id) {
        super(apiClientTransport);
        this.id = id;
    }

    @Override
    public Deployment exec() {
        return apiClientTransport.get("/deployments/" + id, Deployment.class);
    }
}
