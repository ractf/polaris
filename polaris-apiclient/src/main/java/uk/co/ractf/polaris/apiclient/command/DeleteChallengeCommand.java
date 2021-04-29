package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.task.ChallengeDeleteResponse;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class DeleteChallengeCommand extends AbstractCommand<ChallengeDeleteResponse> {

    private final String id;

    public DeleteChallengeCommand(final APIClientTransport apiClientTransport, final String id) {
        super(apiClientTransport);
        this.id = id;
    }

    @Override
    public ChallengeDeleteResponse exec() {
        return apiClientTransport.delete("challenges/" + id, ChallengeDeleteResponse.class);
    }
}
