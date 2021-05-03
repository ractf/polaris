package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class NamespaceGetCommand extends AbstractCommand<Namespace> {

    private final String id;

    public NamespaceGetCommand(final APIClientTransport apiClientTransport, final String id) {
        super(apiClientTransport);
        this.id = id;
    }

    @Override
    public Namespace exec() {
        return apiClientTransport.get("/namespace/" + id, Namespace.class);
    }
}
