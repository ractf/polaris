package uk.co.ractf.polaris.node.runner.docker;

import com.github.dockerjava.api.model.AuthConfig;
import com.google.inject.Singleton;
import uk.co.ractf.polaris.api.authentication.AWSCredentials;
import uk.co.ractf.polaris.api.authentication.ContainerRegistryCredentials;
import uk.co.ractf.polaris.api.authentication.StandardRegistryCredentials;

@Singleton
public class AuthConfigFactory {

    public AuthConfig createAuthConfig(final ContainerRegistryCredentials credentials) {
        if (credentials instanceof StandardRegistryCredentials) {
            return ((StandardRegistryCredentials) credentials).getAuthConfig();
        } else if (credentials instanceof AWSCredentials) {
            new AuthConfig().withRegistrytoken(((AWSCredentials) credentials).getAuthorizationToken());
        }
        return null;
    }
}
