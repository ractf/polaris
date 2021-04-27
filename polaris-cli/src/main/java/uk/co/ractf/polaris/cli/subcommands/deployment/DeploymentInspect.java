package uk.co.ractf.polaris.cli.subcommands.deployment;

import picocli.CommandLine;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.apiclient.APIClient;
import uk.co.ractf.polaris.cli.Subcommand;

@CommandLine.Command(name = "inspect")
public class DeploymentInspect extends Subcommand {

    @CommandLine.Parameters
    public String deploymentId;

    @Override
    public int run(final APIClient apiClient) throws Exception {
        final Deployment deployment = apiClient.getDeployment(deploymentId).exec();
        if (deployment != null) {
            System.out.println(deployment.toJsonString());
        } else {
            System.out.println("Could not find deployment by id " + deploymentId);
        }
        return 0;
    }
}
