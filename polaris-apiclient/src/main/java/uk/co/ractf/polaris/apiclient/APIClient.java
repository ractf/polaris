package uk.co.ractf.polaris.apiclient;

import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.apiclient.command.GetChallengeCommand;
import uk.co.ractf.polaris.apiclient.command.ListChallengesCommand;
import uk.co.ractf.polaris.apiclient.command.PingCommand;
import uk.co.ractf.polaris.apiclient.command.SubmitChallengeCommand;

public interface APIClient {

    static APIClient create(final String apiRoot, final String username, final String password) {
        return new PolarisAPIClient(apiRoot, username, password);
    }

    PingCommand ping();

    ListChallengesCommand listChallenges();

    ListChallengesCommand listChallenges(final String idFilter);

    GetChallengeCommand getChallenge(final String id);

    SubmitChallengeCommand submitChallenge(final Challenge challenge);

}
