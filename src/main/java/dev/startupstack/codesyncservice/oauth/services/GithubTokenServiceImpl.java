package dev.startupstack.codesyncservice.oauth.services;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import dev.startupstack.codesyncservice.oauth.entity.OauthTokenEntity;
import dev.startupstack.codesyncservice.utils.WebResponseBuilder;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

/**
 * GithubTokenService
 */
@ApplicationScoped
@Transactional(REQUIRED)
public class GithubTokenServiceImpl implements GithubTokenService{

	private static final Logger LOG = Logger.getLogger(GithubTokenServiceImpl.class);

    @Inject
    @ConfigProperty(name = "dev.startupstack.codesyncservice.github.client_id")
    String client_id;

    @Inject
    @ConfigProperty(name = "dev.startupstack.codesyncservice.github.client_secret")
    String client_secret;

    @Inject
    @ConfigProperty(name = "dev.startupstack.codesyncservice.github.redirect_url")
    String redirect_url;

    @Override
	public Response processGithubCallback(OauthTokenEntity entity) {

        LOG.infof("[%s] Processing Github response request ...", entity.tenant_id);

        Map<Object, Object> data = new HashMap<>();

        data.put("client_id", client_id);
        data.put("client_secret", client_secret);
        data.put("redirect_url", redirect_url);
        data.put("code", entity.code);
        data.put("state", entity.tenant_id);
      
        final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://github.com/login/oauth/access_token"))
            .timeout(Duration.ofMinutes(1))
            .POST(formDataBuilder(data))
            .header("Accept", "application/json")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build();

        try {

            ObjectMapper objectMapper = new ObjectMapper();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            LOG.debugf("[%s] HTTP response from Github: %s", entity.tenant_id, response.body());

            OauthTokenEntity mappedResponse = objectMapper.readValue(response.body(), OauthTokenEntity.class);

            entity.access_token = mappedResponse.access_token;
            entity.token_type = mappedResponse.token_type;
            entity.scope = mappedResponse.scope;
            entity.persist();
        }
        catch (JsonProcessingException jpe) {
            LOG.errorf("[%s] Processing Github response request: FAILED - %s", entity.tenant_id, jpe.getMessage());
            return WebResponseBuilder.build(jpe.getMessage(), Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } catch (IOException | InterruptedException exception) {
            LOG.errorf("[%s] Processing Github response request: FAILED - %s", entity.tenant_id, exception.getMessage());
            return WebResponseBuilder.build(exception.getMessage(), Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
        
        LOG.infof("[%s] Proccessing Github response request: OK", entity.tenant_id);
        return Response.ok().build();

    }

    static HttpRequest.BodyPublisher formDataBuilder(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    @Override
    public Response getAccessToken(String tenantID) {
        LOG.infof("[%s] Retrieving access token ...", tenantID);
        OauthTokenEntity entity = OauthTokenEntity.find("tenant_id", tenantID).withLock(LockModeType.PESSIMISTIC_WRITE).firstResult();

        if (entity == null) {
            LOG.warnf("[%s] Retrieving access token: NOT FOUND", tenantID);
			return Response.status(Status.NOT_FOUND).build();
        }
        LOG.infof("[%s] Retrieving access token: OK", tenantID);
        return Response.ok(entity.access_token).build();
    }

    @Override
    public Response deleteAccessToken(String tenantID) {
        // TODO Auto-generated method stub
        return null;
    }
}