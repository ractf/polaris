package uk.co.ractf.polaris.security;

import com.google.inject.Singleton;
import io.dropwizard.auth.Authorizer;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class PolarisAuthorizer implements Authorizer<PolarisUser> {

    @Override
    public boolean authorize(final PolarisUser principal, final String role) {
        return authorize(principal, role, null);
    }

    @Override
    public boolean authorize(final PolarisUser principal, final String role,
                             @Nullable final ContainerRequestContext requestContext) {
        return principal.isRoot() || principal.getApiToken().getRoles().contains(role);
    }

}
