package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.deployment.DeploymentSubmitResponse;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class SubmitDeploymentCommand extends AbstractCommand<DeploymentSubmitResponse> {

    private final Deployment deployment;

    public SubmitDeploymentCommand(final APIClientTransport apiClientTransport, final Deployment deployment) {
        super(apiClientTransport);
        this.deployment = deployment;
    }

    @Override
    public DeploymentSubmitResponse exec() {
        return apiClientTransport.post("/deployments", deployment, DeploymentSubmitResponse.class);
    }
}
