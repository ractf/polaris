package uk.co.ractf.polaris.apiclient.command;

import com.fasterxml.jackson.core.type.TypeReference;
import uk.co.ractf.polaris.apiclient.AbstractCommand;
import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

import java.util.Map;

public class PingCommand extends AbstractCommand<Map<String, String>> {

    public PingCommand(final APIClientTransport apiClientTransport) {
        super(apiClientTransport);
    }

    @Override
    public Map<String, String> exec() {
        return apiClientTransport.get("ping", new TypeReference<>() {});
    }
}
