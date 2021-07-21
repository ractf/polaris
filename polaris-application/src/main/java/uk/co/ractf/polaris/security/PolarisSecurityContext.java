package uk.co.ractf.polaris.security;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;

public class PolarisSecurityContext implements SecurityContext {

    private final PolarisUser user;
    private final SecurityContext securityContext;

    public PolarisSecurityContext(final PolarisUser user, final SecurityContext securityContext) {
        this.user = user;
        this.securityContext = securityContext;
    }

    @Override
    public Principal getUserPrincipal() {
        return user;
    }

    @Override
    public boolean isUserInRole(final String role) {
        return user.isRoot() || user.getApiToken().getRoles().contains(role);
    }

    @Override
    public boolean isSecure() {
        return securityContext.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }

    public boolean isUserInNamespace(final String namespace) {
        return user.isRoot() || user.getApiToken().getAllowedNamespaces().contains(namespace);
    }

    public boolean isRoot() {
        return user.isRoot();
    }

    public List<String> getNamespaces() {
        return user.getApiToken().getAllowedNamespaces();
    }

}
