package uk.co.ractf.polaris.sentry;

import io.sentry.DefaultSentryClientFactory;
import io.sentry.SentryClient;
import io.sentry.dsn.Dsn;

public class PolarisSentryClientFactory extends DefaultSentryClientFactory {

    @Override
    public SentryClient createSentryClient(final Dsn dsn) {
        final SentryClient sentryClient = super.createSentryClient(dsn);
        sentryClient.setRelease("@version@");
        return sentryClient;
    }
}
