package dev.startupstack.codesyncservice.oauth.services;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Response;

import dev.startupstack.codesyncservice.oauth.entity.OauthTokenEntity;

/**
 * GithubTokenService
 */
@Dependent
public interface GithubTokenService {

    public Response processGithubCallback(OauthTokenEntity entity);

    public Response getAccessToken(String tenant_id);

    public Response deleteAccessToken(String tenant_id);

}