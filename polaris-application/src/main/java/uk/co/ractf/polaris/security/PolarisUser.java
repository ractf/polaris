package uk.co.ractf.polaris.security;

import uk.co.ractf.polaris.api.authentication.APIToken;

import java.security.Principal;

public class PolarisUser implements Principal {

    private final String name;
    private final APIToken apiToken;
    private final boolean root;

    public PolarisUser(final String name, final APIToken apiToken, final boolean root) {
        this.name = name;
        this.apiToken = apiToken;
        this.root = root;
    }

    @Override
    public String getName() {
        return name;
    }

    public APIToken getApiToken() {
        return apiToken;
    }

    public boolean isRoot() {
        return root;
    }
}
