package uk.co.ractf.polaris.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.auth.Authorizer;
import org.jetbrains.annotations.Nullable;
import uk.co.ractf.polaris.controller.ControllerConfiguration;

import javax.ws.rs.container.ContainerRequestContext;

@Singleton
@SuppressWarnings("deprecation")
public class PolarisAuthorizer implements Authorizer<PolarisUser> {

    private final ControllerConfiguration controllerConfiguration;

    @Inject
    public PolarisAuthorizer(final ControllerConfiguration controllerConfiguration) {
        this.controllerConfiguration = controllerConfiguration;
    }

    @Override
    public boolean authorize(final PolarisUser principal, final String role) {
        return authorize(principal, role, null);
    }

    @Override
    public boolean authorize(final PolarisUser principal, final String role,
                             @Nullable final ContainerRequestContext requestContext) {
        return controllerConfiguration.getSingleUserRoles().contains(role);
    }

}
