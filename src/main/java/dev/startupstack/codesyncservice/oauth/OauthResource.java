package dev.startupstack.codesyncservice.oauth;

import java.io.IOException;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import dev.startupstack.codesyncservice.oauth.entity.OauthTokenEntity;
import dev.startupstack.codesyncservice.oauth.services.GithubTokenService;

/**
 * OauthResource
 */
@Path("/oauth")
@Tag(name = "oauth")
@Dependent
public class OauthResource {

    @Inject
    GithubTokenService githubTokenService;

    @Path("/github")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response processGithubCallback(@QueryParam("code") final String code, @QueryParam("state") final String state, @QueryParam("error") final String error) throws IOException {

        OauthTokenEntity entity = new OauthTokenEntity();
        entity.code = code;
        entity.tenant_id = state;
        entity.error = error;

        return githubTokenService.processGithubCallback(entity);
        
    }
    @Path("/github/token/{tenant_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getGithubAccessToken(@PathParam("tenant_id") final String tenantID) {
        return githubTokenService.getAccessToken(tenantID);
    }
    @Path("/github/token/{tenant_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE    
    public Response deleteGithubAccessToken(@PathParam("tenant_id") final String tenantID) {
        return null;
    }
    
}