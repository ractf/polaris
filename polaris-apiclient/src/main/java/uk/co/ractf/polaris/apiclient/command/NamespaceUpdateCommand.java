package uk.co.ractf.polaris.apiclient.command;

import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.api.namespace.NamespaceUpdateResponse;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public class NamespaceUpdateCommand extends AbstractCommand<NamespaceUpdateResponse> {

    private final Namespace namespace;

    public NamespaceUpdateCommand(final APIClientTransport apiClientTransport, final Namespace namespace) {
        super(apiClientTransport);
        this.namespace = namespace;
    }

    @Override
    public NamespaceUpdateResponse exec() {
        return apiClientTransport.put("/namespaces/" + namespace.getName(), namespace, NamespaceUpdateResponse.class);
    }
}
