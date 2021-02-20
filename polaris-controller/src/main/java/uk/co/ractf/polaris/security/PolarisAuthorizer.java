package uk.co.ractf.polaris.security;

import io.dropwizard.auth.Authorizer;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.container.ContainerRequestContext;

public class PolarisAuthorizer implements Authorizer <PolarisUser> {

    @Override
    public boolean authorize(final PolarisUser principal, final String role) {
        return true;
    }

    @Override
    public boolean authorize(final PolarisUser principal, final String role,
                             @Nullable final ContainerRequestContext requestContext) {
        return true;
    }

}
