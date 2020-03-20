package dev.startupstack.codesyncservice.oauth.models;

/**
 * AccessTokenResponseModel
 */
public class AccessTokenResponseModel {

    private String accessToken;


    public AccessTokenResponseModel() {
    }

    public AccessTokenResponseModel(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public AccessTokenResponseModel accessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

}