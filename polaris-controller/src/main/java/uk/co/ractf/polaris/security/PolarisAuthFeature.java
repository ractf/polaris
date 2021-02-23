package uk.co.ractf.polaris.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ext.Provider;

@Singleton
@Provider
public class PolarisAuthFeature extends AuthDynamicFeature {

    @Inject
    public PolarisAuthFeature(final PolarisAuthenticator authenticator,
                              final PolarisAuthorizer authorizer,
                              final Environment environment) {
        super(new BasicCredentialAuthFilter.Builder<PolarisUser>()
                .setAuthenticator(authenticator)
                .setAuthorizer(authorizer)
                .setRealm("POLARIS")
                .buildAuthFilter()
        );

        environment.jersey().register(RolesAllowedDynamicFeature.class);
    }

}
