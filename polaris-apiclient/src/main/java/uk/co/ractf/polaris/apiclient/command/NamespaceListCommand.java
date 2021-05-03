package uk.co.ractf.polaris.apiclient.command;

import com.fasterxml.jackson.core.type.TypeReference;
import uk.co.ractf.polaris.api.namespace.Namespace;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

import java.util.Map;

public class NamespaceListCommand extends AbstractCommand<Map<String, Namespace>> {
    public NamespaceListCommand(final APIClientTransport apiClientTransport) {
        super(apiClientTransport);
    }

    @Override
    public Map<String, Namespace> exec() {
        return apiClientTransport.get("/namespaces", new TypeReference<>(){});
    }
}
