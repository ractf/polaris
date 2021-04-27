package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.deployment.DeploymentDeleteResponse;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class DeleteDeploymentCommand extends AbstractCommand<DeploymentDeleteResponse> {

    private final String id;

    public DeleteDeploymentCommand(final APIClientTransport apiClientTransport, final String id) {
        super(apiClientTransport);
        this.id = id;
    }

    @Override
    public DeploymentDeleteResponse exec() {
        return apiClientTransport.delete("/deployments/" + id, DeploymentDeleteResponse.class);
    }
}
