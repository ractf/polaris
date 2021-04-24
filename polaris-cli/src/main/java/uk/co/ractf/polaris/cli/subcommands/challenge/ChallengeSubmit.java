package uk.co.ractf.polaris.cli.subcommands.challenge;

import picocli.CommandLine;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.api.challenge.ChallengeSubmitResponse;
import uk.co.ractf.polaris.apiclient.APIClient;
import uk.co.ractf.polaris.cli.Subcommand;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@CommandLine.Command(name = "submit")
public class ChallengeSubmit extends Subcommand {

    @CommandLine.Parameters
    private List<File> files;

    @Override
    public int run(final APIClient apiClient) throws Exception {
        for (final File file : files) {
            final Challenge challenge = Challenge.parse(Files.readString(file.toPath()), Challenge.class);
            final ChallengeSubmitResponse response = apiClient.submitChallenge(challenge).exec();
            if (response.getStatus() == ChallengeSubmitResponse.Status.OK) {
                System.out.println("Created challenge " + response.getId());
            } else if (response.getStatus() == ChallengeSubmitResponse.Status.DUPLICATE) {
                System.out.println("Duplicated challenge id " + response.getId());
            } else if (response.getStatus() == ChallengeSubmitResponse.Status.INVALID) {
                System.out.println("Invalid challenge spec " + response.getId());
            }
        }
        return 0;
    }
}
