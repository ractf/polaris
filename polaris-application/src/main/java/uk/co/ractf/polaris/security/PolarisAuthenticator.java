package uk.co.ractf.polaris.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import uk.co.ractf.polaris.CommonConfiguration;
import uk.co.ractf.polaris.state.ClusterState;

import java.util.Optional;

@Singleton
public class PolarisAuthenticator implements Authenticator<BasicCredentials, PolarisUser> {

    private final CommonConfiguration config;
    private final ClusterState clusterState;

    @Inject
    public PolarisAuthenticator(final CommonConfiguration config, final ClusterState clusterState) {
        this.config = config;
        this.clusterState = clusterState;
    }

    @Override
    public Optional<PolarisUser> authenticate(final BasicCredentials credentials) {
        if (config.getSingleUserUsername().equals(credentials.getUsername()) &&
                config.getSingleUserPassword().equals(credentials.getPassword())) {
            return Optional.of(new PolarisUser("root", null, true));
        }

        final var apiToken = clusterState.getAPIToken(credentials.getUsername());
        if (apiToken == null || !apiToken.getToken().equals(credentials.getPassword())) {
            return Optional.empty();
        }
        return Optional.of(new PolarisUser(credentials.getUsername(), apiToken, false));
    }

}
