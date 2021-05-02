package uk.co.ractf.polaris.cli.subcommands.deployment;

import picocli.CommandLine;
import uk.co.ractf.polaris.apiclient.APIClient;
import uk.co.ractf.polaris.cli.Subcommand;

import java.util.List;

@CommandLine.Command(name = "delete", aliases = {"rm"})
public class DeploymentDelete extends Subcommand {

    @CommandLine.Parameters
    private List<String> deployments;

    @Override
    public int run(final APIClient apiClient) throws Exception {
        for (final String deployment : deployments) {
            final DeploymentDeleteResponse response = apiClient.deleteDeployment(deployment).exec();
            if (response.getStatus() == DeploymentDeleteResponse.Status.OK) {
                System.out.println("Deleted deployment " + response.getId());
            } else if (response.getStatus() == DeploymentDeleteResponse.Status.NOT_FOUND) {
                System.out.println("Could not find deployment " + response.getId());
            }
        }
        return 0;
    }
}
