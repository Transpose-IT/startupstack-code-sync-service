package dev.startupstack.codesyncservice.sync.models;

import static dev.startupstack.codesyncservice.Constants.REPO_TYPE_GITHUB;

import javax.ws.rs.WebApplicationException;

/**
 * SyncRequestModel
 */
public class SyncRequestModel {

    private String repositoryType;
    private Long repositoryID;
    private String tenantID;


    public SyncRequestModel() {
    }

    public SyncRequestModel(String repositoryType, Long repositoryID) {
        this.repositoryType = repositoryType;
        this.repositoryID = repositoryID;
    }

    public String getRepositoryType() {
        return this.repositoryType;
    }

    public void setRepositoryType(String repositoryType) {
        if (repositoryType.equals("github")) {
            this.repositoryType = REPO_TYPE_GITHUB;
        } else {
            throw new WebApplicationException("invalid repository type passed: " + repositoryType);
        }
    }

    public Long getRepositoryID() {
        return this.repositoryID;
    }

    public void setRepositoryID(Long repositoryID) {
        this.repositoryID = repositoryID;
    }

    public String getTenantID() {
        return this.tenantID;
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

}