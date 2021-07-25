package uk.co.ractf.polaris.controller.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import uk.co.ractf.polaris.api.registry.credentials.ContainerRegistryCredentials;
import uk.co.ractf.polaris.api.registry.credentials.CredentialsDeleteResponse;
import uk.co.ractf.polaris.api.registry.credentials.CredentialsSubmitResponse;
import uk.co.ractf.polaris.api.registry.credentials.CredentialsUpdateResponse;
import uk.co.ractf.polaris.api.namespace.NamespacedId;
import uk.co.ractf.polaris.security.PolarisSecurityContext;
import uk.co.ractf.polaris.security.PolarisUser;
import uk.co.ractf.polaris.state.ClusterState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Path("/credentials")
@Produces(MediaType.APPLICATION_JSON)
public class CredentialResource extends SecureResource {

    private final ClusterState clusterState;

    @Inject
    public CredentialResource(final ClusterState clusterState) {
        this.clusterState = clusterState;
    }

    @GET
    @Timed
    @ExceptionMetered
    @RolesAllowed("CREDENTIAL_GET")
    @Operation(summary = "Get Credentials", tags = {"Credential"},
            description = "Gets a map of credential id to credential within a given namespace which can be filtered by type or id regex.")
    public Map<NamespacedId, ContainerRegistryCredentials> getCredentials(@Context final SecurityContext securityContext,
                                                                          @QueryParam("namespace") @DefaultValue("") final String namespace,
                                                                          @QueryParam("filter") @DefaultValue("") final String filter,
                                                                          @QueryParam("type") @DefaultValue("") final String type) {
        final var context = convertContext(securityContext);
        final Map<NamespacedId, ContainerRegistryCredentials> credentialsMap;

        if (namespace.isEmpty()) {
            if (context.isRoot()) {
                credentialsMap = clusterState.getCredentials();
            } else {
                credentialsMap = new HashMap<>();
                for (final var namespaceId : context.getNamespaces()) {
                    credentialsMap.putAll(clusterState.getCredentials(namespaceId));
                }
            }
        } else {
            if (!context.isUserInNamespace(namespace)) {
                throw new WebApplicationException(Response.Status.FORBIDDEN);
            }
            credentialsMap = clusterState.getCredentials(namespace);
        }

        if (filter.isEmpty() && type.isEmpty()) {
            return credentialsMap;
        }

        final var pattern = Pattern.compile(filter);
        final var filtered = new HashMap<NamespacedId, ContainerRegistryCredentials>();
        for (final var entry : credentialsMap.entrySet()) {
            var matches = filter.isEmpty() || pattern.matcher(entry.getKey().getId()).find();
            matches &= type.isEmpty() || entry.getValue().getType().equalsIgnoreCase(type);
            if (matches) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }

        return filtered;
    }

    @GET
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("CREDENTIAL_GET")
    @Operation(summary = "Get Credential", tags = {"Credential"}, description = "Gets a set of credentials by given id")
    public ContainerRegistryCredentials getCredential(@Context final SecurityContext securityContext, @PathParam("id") final NamespacedId id) {
        final var context = convertContext(securityContext);
        if (context.isUserInNamespace(id.getNamespace())) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        final var credential = clusterState.getCredential(id);
        if (credential == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return credential;
    }

    @POST
    @Timed
    @ExceptionMetered
    @RolesAllowed("CREDENTIAL_ADD")
    @Operation(summary = "Add Credential", tags = {"Credential"}, description = "Adds a credential")
    public Response addCredential(@Context final SecurityContext securityContext, @RequestBody final ContainerRegistryCredentials credential) {
        final var context = convertContext(securityContext);
        if (!context.isUserInNamespace(credential.getId().getNamespace())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new CredentialsSubmitResponse(CredentialsSubmitResponse.Status.FORBIDDEN_NAMESPACE, credential.getId())).build();
        } else if (clusterState.getNamespace(credential.getId().getNamespace()) == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CredentialsSubmitResponse(CredentialsSubmitResponse.Status.BAD_NAMESPACE, credential.getId())).build();
        }

        if (clusterState.getCredential(credential.getId()) != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CredentialsSubmitResponse(CredentialsSubmitResponse.Status.DUPLICATE, credential.getId())).build();
        }

        clusterState.setCredential(credential);
        return Response.status(Response.Status.CREATED)
                .entity(new CredentialsSubmitResponse(CredentialsSubmitResponse.Status.OK, credential.getId())).build();
    }

    @PUT
    @Timed
    @ExceptionMetered
    @RolesAllowed("CREDENTIAL_UPDATE")
    @Operation(summary = "Update Credential", tags = {"Credential"}, description = "Modifies a credential")
    public Response updateCredential(@Context final SecurityContext securityContext, @RequestBody final ContainerRegistryCredentials credentials) {
        final var context = convertContext(securityContext);
        if (!context.isUserInNamespace(credentials.getId().getNamespace())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new CredentialsUpdateResponse(CredentialsUpdateResponse.Status.FORBIDDEN_NAMESPACE, credentials.getId())).build();
        }

        if (clusterState.getCredential(credentials.getId()) == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CredentialsUpdateResponse(CredentialsUpdateResponse.Status.NOT_FOUND, credentials.getId())).build();
        }

        clusterState.setCredential(credentials);
        return Response.ok(new CredentialsUpdateResponse(CredentialsUpdateResponse.Status.OK, credentials.getId())).build();
    }

    @DELETE
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @RolesAllowed("CREDENTIAL_DELETE")
    @Operation(summary = "Delete Credential", tags = {"Credential"}, description = "Deletes a credential")
    public Response deleteCredential(@Context final SecurityContext securityContext, @PathParam("id") final NamespacedId id) {
        final var context = convertContext(securityContext);
        if (!context.isUserInNamespace(id.getNamespace())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new CredentialsDeleteResponse(CredentialsDeleteResponse.Status.FORBIDDEN_NAMESPACE, id)).build();
        }

        if (clusterState.getCredential(id) == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CredentialsDeleteResponse(CredentialsDeleteResponse.Status.NOT_FOUND, id)).build();
        }

        clusterState.deleteCredential(id);
        return Response.ok(new CredentialsDeleteResponse(CredentialsDeleteResponse.Status.OK, id)).build();
    }

}
