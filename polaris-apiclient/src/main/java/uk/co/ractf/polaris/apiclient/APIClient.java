package uk.co.ractf.polaris.apiclient;

import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.apiclient.command.*;

public interface APIClient {

    static APIClient create(final String apiRoot, final String username, final String password) {
        return new PolarisAPIClient(apiRoot, username, password);
    }

    PingCommand ping();

    ListChallengesCommand listChallenges();

    ListChallengesCommand listChallenges(final String idFilter);

    GetChallengeCommand getChallenge(final String id);

    SubmitChallengeCommand submitChallenge(final Challenge challenge);

    DeleteChallengeCommand deleteChallenge(final String id);

    ListDeploymentsCommand listDeployments();

    ListDeploymentsCommand listDeployments(final String idFilter);

    ListDeploymentsCommand listDeployments(final String idFilter, final String challengeIdFilter);

    GetDeploymentCommand getDeployment(final String id);

    SubmitDeploymentCommand submitDeployment(final Deployment deployment);

    DeleteDeploymentCommand deleteDeployment(final String id);

}
