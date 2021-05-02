package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.task.Challenge;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class SubmitChallengeCommand extends AbstractCommand<ChallengeSubmitResponse> {

    private final Challenge challenge;

    public SubmitChallengeCommand(final APIClientTransport apiClientTransport, final Challenge challenge) {
        super(apiClientTransport);
        this.challenge = challenge;
    }

    @Override
    public ChallengeSubmitResponse exec() {
        return apiClientTransport.post("/challenges", challenge, ChallengeSubmitResponse.class);
    }
}
