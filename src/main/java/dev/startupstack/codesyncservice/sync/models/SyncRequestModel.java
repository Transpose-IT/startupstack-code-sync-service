package dev.startupstack.codesyncservice.sync.models;

/**
 * SyncRequestModel
 */
public class SyncRequestModel {

    private String repositoryType;
    private String repositoryID;


    public SyncRequestModel() {
    }

    public SyncRequestModel(String repositoryType, String repositoryID) {
        this.repositoryType = repositoryType;
        this.repositoryID = repositoryID;
    }

    public String getRepositoryType() {
        return this.repositoryType;
    }

    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }

    public String getRepositoryID() {
        return this.repositoryID;
    }

    public void setRepositoryID(String repositoryID) {
        this.repositoryID = repositoryID;
    }

}