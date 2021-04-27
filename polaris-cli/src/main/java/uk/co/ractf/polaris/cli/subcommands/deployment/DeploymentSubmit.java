package uk.co.ractf.polaris.cli.subcommands.deployment;

import picocli.CommandLine;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.api.deployment.DeploymentSubmitResponse;
import uk.co.ractf.polaris.apiclient.APIClient;
import uk.co.ractf.polaris.cli.Subcommand;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@CommandLine.Command(name = "submit")
public class DeploymentSubmit extends Subcommand {

    @CommandLine.Parameters
    private List<File> files;

    @Override
    public int run(final APIClient apiClient) throws Exception {
        for (final File file : files) {
            final Deployment deployment = Deployment.parse(Files.readString(file.toPath()), Deployment.class);
            final DeploymentSubmitResponse response = apiClient.submitDeployment(deployment).exec();

            switch (response.getStatus()) {
                case OK:
                    System.out.println("Created deployment " + response.getId());
                    break;
                case DUPLICATE:
                    System.out.println("Duplicated deployment id " + response.getId());
                    break;
                case INVALID:
                    System.out.println("Invalid deployment spec " + response.getId());
                    break;
                case REJECTED:
                    System.out.println("Deployment rejected " + response.getId());
                    break;
            }
        }
        return 0;
    }
}
