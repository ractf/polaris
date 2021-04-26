package uk.co.ractf.polaris.cli.subcommands.challenge;

import picocli.CommandLine;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.apiclient.APIClient;
import uk.co.ractf.polaris.cli.Subcommand;

@CommandLine.Command(name = "inspect")
public class ChallengeInspect extends Subcommand {

    @CommandLine.Parameters
    public String challengeId;

    @Override
    public int run(final APIClient apiClient) throws Exception {
        final Challenge challenge = apiClient.getChallenge(challengeId).exec();
        if (challenge != null) {
            System.out.println(challenge.toJsonString());
        } else {
            System.out.println("Could not find challenge by id " + challengeId);
        }
        return 0;
    }
}
