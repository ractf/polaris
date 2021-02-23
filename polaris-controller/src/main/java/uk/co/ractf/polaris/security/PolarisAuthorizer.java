package uk.co.ractf.polaris.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.auth.Authorizer;
import org.jetbrains.annotations.Nullable;
import uk.co.ractf.polaris.PolarisConfiguration;

import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class PolarisAuthorizer implements Authorizer <PolarisUser> {

    private final PolarisConfiguration polarisConfiguration;

    @Inject
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
