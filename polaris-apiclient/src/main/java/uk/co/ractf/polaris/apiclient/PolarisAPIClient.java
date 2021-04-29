package uk.co.ractf.polaris.apiclient;

import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.api.deployment.Deployment;
import uk.co.ractf.polaris.apiclient.command.*;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;
import uk.co.ractf.polaris.apiclient.transport.HttpClientTransport;

public class PolarisAPIClient implements APIClient {

    private final APIClientTransport transport;

    PolarisAPIClient(final String apiRoot, final String username, final String password) {
        this.transport = new HttpClientTransport(apiRoot, username, password);
    }

    @Override
    public PingCommand ping() {
        return new PingCommand(transport);
    }

    @Override
    public ListChallengesCommand listChallenges() {
        return new ListChallengesCommand(transport);
    }

    @Override
    public ListChallengesCommand listChallenges(final String idFilter) {
        return new ListChallengesCommand(transport).withIdFilter(idFilter);
    }

    @Override
    public GetChallengeCommand getChallenge(final String id) {
        return new GetChallengeCommand(transport, id);
    }

    @Override
    public SubmitChallengeCommand submitChallenge(final Challenge challenge) {
        return new SubmitChallengeCommand(transport, challenge);
    }

    @Override
    public DeleteChallengeCommand deleteChallenge(final String id) {
        return new DeleteChallengeCommand(transport, id);
    }

    @Override
    public ListDeploymentsCommand listDeployments() {
        return new ListDeploymentsCommand(transport);
    }

    @Override
    public ListDeploymentsCommand listDeployments(final String idFilter) {
        return new ListDeploymentsCommand(transport).withIdFilter(idFilter);
    }

    @Override
    public ListDeploymentsCommand listDeployments(final String idFilter, final String challengeIdFilter) {
        return new ListDeploymentsCommand(transport).withIdFilter(idFilter).withChallengeIdFilter(challengeIdFilter);
    }

    @Override
    public GetDeploymentCommand getDeployment(final String id) {
        return new GetDeploymentCommand(transport, id);
    }

    @Override
    public SubmitDeploymentCommand submitDeployment(final Deployment deployment) {
        return new SubmitDeploymentCommand(transport, deployment);
    }

    @Override
    public DeleteDeploymentCommand deleteDeployment(final String id) {
        return new DeleteDeploymentCommand(transport, id);
    }

}
