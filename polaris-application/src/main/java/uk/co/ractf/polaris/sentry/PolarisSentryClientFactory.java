package uk.co.ractf.polaris.sentry;

import io.sentry.DefaultSentryClientFactory;
import io.sentry.SentryClient;
import io.sentry.dsn.Dsn;
import uk.co.ractf.polaris.util.PolarisVersion;

public class PolarisSentryClientFactory extends DefaultSentryClientFactory {

    @Override
    public SentryClient createSentryClient(final Dsn dsn) {
        final SentryClient sentryClient = super.createSentryClient(dsn);
        sentryClient.setRelease(PolarisVersion.VERSION);
        return sentryClient;
    }
}
