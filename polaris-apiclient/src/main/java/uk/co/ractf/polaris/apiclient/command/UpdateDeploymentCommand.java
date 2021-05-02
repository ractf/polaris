package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class UpdateDeploymentCommand extends AbstractCommand<DeploymentUpdateResponse> {

    private final Deployment deployment;

    public UpdateDeploymentCommand(final APIClientTransport apiClientTransport, final Deployment deployment) {
        super(apiClientTransport);
        this.deployment = deployment;
    }

    @Override
    public DeploymentUpdateResponse exec() {
        return apiClientTransport.put("/deployments", deployment, DeploymentUpdateResponse.class);
    }
}
