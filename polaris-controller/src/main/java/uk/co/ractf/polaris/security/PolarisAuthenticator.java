package uk.co.ractf.polaris.security;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import uk.co.ractf.polaris.PolarisConfiguration;

import java.util.Optional;

public class PolarisAuthenticator implements Authenticator<BasicCredentials, PolarisUser> {

    private final PolarisConfiguration polarisConfiguration;

    public PolarisAuthenticator(final PolarisConfiguration polarisConfiguration) {
        this.polarisConfiguration = polarisConfiguration;
    }

    @Override
    public Optional<PolarisUser> authenticate(final BasicCredentials credentials) throws AuthenticationException {
        if (polarisConfiguration.getSingleUserUsername().equals(credentials.getUsername()) &&
                polarisConfiguration.getSingleUserPassword().equals(credentials.getPassword())) {
            return Optional.of(new PolarisUser(credentials.getUsername()));
        }
        return Optional.empty();
    }

}
