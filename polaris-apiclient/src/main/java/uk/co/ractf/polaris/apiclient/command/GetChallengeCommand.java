package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.challenge.Challenge;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class GetChallengeCommand extends AbstractCommand<Challenge> {

    private final String id;

    public GetChallengeCommand(final APIClientTransport apiClientTransport, final String id) {
        super(apiClientTransport);
        this.id = id;
    }

    @Override
    public Challenge exec() {
        return apiClientTransport.get("/challenge/" + id, Challenge.class);
    }
}
