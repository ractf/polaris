package uk.co.ractf.polaris.apiclient;

import uk.co.ractf.polaris.apiclient.transport.APIClientTransport;

public abstract class AbstractCommand<T> {

    protected final APIClientTransport apiClientTransport;

    public AbstractCommand(final APIClientTransport apiClientTransport) {
        this.apiClientTransport = apiClientTransport;
    }

    public abstract T exec();

}
