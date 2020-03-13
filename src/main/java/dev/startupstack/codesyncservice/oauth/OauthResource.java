package dev.startupstack.codesyncservice.oauth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import dev.startupstack.codesyncservice.oauth.models.GithubAccessModel;
import dev.startupstack.codesyncservice.oauth.models.GithubTokenResponse;
import dev.startupstack.codesyncservice.utils.WebResponseBuilder;

/**
 * OauthResource
 */
@Path("/oauth")
@Dependent
public class OauthResource {

    @Path("callback")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response callback(@QueryParam("code") final String code, @QueryParam("state") final String state) throws IOException {

        GithubAccessModel model = new GithubAccessModel();
        model.setCode(code);
        model.setState(state);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://github.com/login/oauth/access_token");
        httpPost.setHeader("Accept", "application/json");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", model.getClientID()));
        params.add(new BasicNameValuePair("client_secret", model.getClientSecret()));
        params.add(new BasicNameValuePair("code", model.getCode()));
        params.add(new BasicNameValuePair("redirect_uri", model.getRedirectUri()));
        params.add(new BasicNameValuePair("state", model.getState()));

        ObjectMapper objectMapper = new ObjectMapper();

        
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = client.execute(httpPost);
        GithubTokenResponse githubResponse = objectMapper.readValue(EntityUtils.toString(response.getEntity()), GithubTokenResponse.class);

        if (githubResponse.getError() != null) {
            System.out.println(githubResponse.getError_description());
        }

        model.setAccessToken(githubResponse.getAccess_token());
        model.setScope(githubResponse.getScope());
        model.setTokenType(githubResponse.getToken_type());

        client.close();

        System.out.println();


        return Response.status(Status.OK).entity(model).build();
    }
    
}