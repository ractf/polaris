package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.api.namespace.NamespaceCreateResponse;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class NamespaceCreateCommand extends AbstractCommand<NamespaceCreateResponse> {

    private final Namespace namespace;

    public NamespaceCreateCommand(final APIClientTransport apiClientTransport, final Namespace namespace) {
        super(apiClientTransport);
        this.namespace = namespace;
    }

    @Override
    public NamespaceCreateResponse exec() {
        return apiClientTransport.post("/namespaces", namespace, NamespaceCreateResponse.class);
    }
}
