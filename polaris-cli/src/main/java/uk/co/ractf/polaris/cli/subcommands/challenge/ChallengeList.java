package uk.co.ractf.polaris.cli.subcommands.challenge;

import picocli.CommandLine;
import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.apiclient.APIClient;
import uk.co.ractf.polaris.cli.Subcommand;

import java.util.Map;

@CommandLine.Command(name = "list", aliases = {"ls"})
public class ChallengeList extends Subcommand {

    @Override
    public int run(final APIClient apiClient) {
        final Map<String, Challenge> challengeMap = apiClient.listChallenges().exec();
        for (final String key : challengeMap.keySet()) {
            System.out.println(key);
        }
        return 0;
    }
}
