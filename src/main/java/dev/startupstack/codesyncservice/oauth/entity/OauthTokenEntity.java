package dev.startupstack.codesyncservice.oauth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * OauthInfoEntity
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class OauthTokenEntity extends PanacheEntity {
    public String code;
    public String tenant_id;

    @Column(columnDefinition = "TEXT")
    public String access_token;

    public String scope;
    public String token_type;

    public String error;
    public String error_description;
   
}