package uk.co.ractf.polaris.security;

import io.dropwizard.auth.Authorizer;
import org.jetbrains.annotations.Nullable;
import uk.co.ractf.polaris.PolarisConfiguration;

import javax.ws.rs.container.ContainerRequestContext;

public class PolarisAuthorizer implements Authorizer <PolarisUser> {

    private final PolarisConfiguration polarisConfiguration;

    public PolarisAuthorizer(final PolarisConfiguration polarisConfiguration) {
        this.polarisConfiguration = polarisConfiguration;
    }

    @Override
    public boolean authorize(final PolarisUser principal, final String role) {
        return polarisConfiguration.getSingleUserRoles().contains(role);
    }

    @Override
    public boolean authorize(final PolarisUser principal, final String role,
                             @Nullable final ContainerRequestContext requestContext) {
        return polarisConfiguration.getSingleUserRoles().contains(role);
    }

}
