package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.namespace.NamespaceDeleteResponse;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class NamespaceDeleteCommand extends AbstractCommand<NamespaceDeleteResponse> {

    private final String id;

    public NamespaceDeleteCommand(final APIClientTransport apiClientTransport, final String id) {
        super(apiClientTransport);
        this.id = id;
    }

    @Override
    public NamespaceDeleteResponse exec() {
        return apiClientTransport.delete("/namespaces" + id, NamespaceDeleteResponse.class);
    }
}
