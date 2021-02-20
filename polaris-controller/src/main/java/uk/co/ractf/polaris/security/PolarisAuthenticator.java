package uk.co.ractf.polaris.security;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;

public class PolarisAuthenticator implements Authenticator<BasicCredentials, PolarisUser> {

    @Override
    public Optional<PolarisUser> authenticate(final BasicCredentials credentials) throws AuthenticationException {
        return Optional.of(new PolarisUser("root"));
    }

}
