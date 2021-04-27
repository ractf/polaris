package uk.co.ractf.polaris.cli.subcommands.deployment;

import picocli.CommandLine;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.apiclient.APIClient;
import uk.co.ractf.polaris.cli.Subcommand;

import java.util.Map;

@CommandLine.Command(name = "list", aliases = {"ls"})
public class DeploymentList extends Subcommand {

    @CommandLine.Option(names = {"--filter", "-f"})
    private String filter = "";

    @Override
    public int run(final APIClient apiClient) throws Exception {
        final Map<String, Deployment> deployments = apiClient.listDeployments(filter).exec();
        for (final String key : deployments.keySet()) {
            System.out.println(key);
        }
        return 0;
    }
}
