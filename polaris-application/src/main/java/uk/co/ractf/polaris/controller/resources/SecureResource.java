package uk.co.ractf.polaris.controller.resources;

import uk.co.ractf.polaris.security.PolarisSecurityContext;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.lang.reflect.Field;

public abstract class SecureResource {

    private final Field field;

    public SecureResource() {
        try {
            final var clazz = Class.forName("org.glassfish.jersey.server.internal.process.SecurityContextInjectee");
            this.field = clazz.getDeclaredField("requestContext");
            this.field.setAccessible(true);
        } catch (final ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected PolarisSecurityContext convertContext(final SecurityContext securityContext) {
        try {
            final var container = (ContainerRequestContext) field.get(securityContext);
            return (PolarisSecurityContext) container.getSecurityContext();
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
