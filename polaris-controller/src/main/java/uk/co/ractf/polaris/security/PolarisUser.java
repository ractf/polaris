package uk.co.ractf.polaris.security;

import java.security.Principal;

public class PolarisUser implements Principal {

    private final String name;

    public PolarisUser(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
