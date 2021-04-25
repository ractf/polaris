package uk.co.ractf.polaris.cli.subcommands.challenge;

import picocli.CommandLine;
import uk.co.ractf.polaris.api.challenge.ChallengeDeleteResponse;
import uk.co.ractf.polaris.apiclient.APIClient;
import uk.co.ractf.polaris.cli.Subcommand;

import java.util.List;

@CommandLine.Command(name = "delete", aliases = {"rm", "del"})
public class ChallengeDelete extends Subcommand {

    @CommandLine.Parameters
    private List<String> challenges;

    @Override
    public int run(final APIClient apiClient) {
        for (final String challenge : challenges) {
            final ChallengeDeleteResponse response = apiClient.deleteChallenge(challenge).exec();
            if (response.getStatus() == ChallengeDeleteResponse.Status.OK) {
                System.out.println("Deleted challenge " + response.getId());
            } else if (response.getStatus() == ChallengeDeleteResponse.Status.NOT_FOUND) {
                System.out.println("Could not find challenge " + response.getId());
            }
        }
        return 0;
    }
}
