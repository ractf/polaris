package uk.co.ractf.polaris.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import uk.co.ractf.polaris.CommonConfiguration;

import java.util.Optional;

@Singleton
public class PolarisAuthenticator implements Authenticator<BasicCredentials, PolarisUser> {

    private final CommonConfiguration config;

    @Inject
    public PolarisAuthenticator(final CommonConfiguration config) {
        this.config = config;
    }

    @Override
    public Optional<PolarisUser> authenticate(final BasicCredentials credentials) {
        if (config.getSingleUserUsername().equals(credentials.getUsername()) &&
                config.getSingleUserPassword().equals(credentials.getPassword())) {
            return Optional.of(new PolarisUser(credentials.getUsername()));
        }
        return Optional.empty();
    }

}
