/** 
* This file is part of startupstack.
* Copyright (c) 2020-2022, Transpose-IT B.V.
*
* Startupstack is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Startupstack is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You can find a copy of the GNU General Public License in the
* LICENSE file.  Alternatively, see <http://www.gnu.org/licenses/>.
*/
package dev.startupstack.codesyncservice.sync;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jboss.logging.Logger;

import dev.startupstack.codesyncservice.github.GitHubService;
import dev.startupstack.codesyncservice.github.entity.GitHubEntity;
import dev.startupstack.codesyncservice.oauth.models.AccessTokenResponseModel;
import dev.startupstack.codesyncservice.oauth.services.GithubTokenService;
import dev.startupstack.codesyncservice.sync.models.SyncRequestModel;
import dev.startupstack.codesyncservice.utils.ArchiveUtils;
import dev.startupstack.codesyncservice.utils.WebResponseBuilder;

import static dev.startupstack.codesyncservice.Constants.REPO_TYPE_GITHUB;

/**
 * GitSyncServiceImpl
 */
@Dependent
public class SyncServiceImpl implements SyncService {

    @Inject
    GithubTokenService githubTokenService;

    @Inject
    GitHubService githubService;

    private static final Logger LOG = Logger.getLogger(SyncServiceImpl.class);

    @Override
    public Response syncRepository(SyncRequestModel model) {

        LOG.infof("[%s] Syncing repository id '%s': starting ...", model.getTenantID(), model.getRepositoryID());

        File workDir = new File("/tmp/" + model.getTenantID());
        File checkoutDir = new File(workDir + "/checkout");
        String tarball = String.format("%s/%s.tar.gz", workDir, UUID.randomUUID().toString());

        try {
            if (model.getRepositoryType() == REPO_TYPE_GITHUB) {
                cloneGitHubRepository(checkoutDir, model);
            } else {
                return WebResponseBuilder.build("Invalid repository type passed", Status.INTERNAL_SERVER_ERROR.getStatusCode());
            }
            LOG.infof("[%s] Syncing repository id '%s': Creating tarball '%s'", model.getTenantID(), model.getRepositoryID(), tarball);
            ArchiveUtils.createTarGZ(tarball, checkoutDir);


        } catch (GitAPIException gae) {

            LOG.errorf("[%s] Syncing repository id '%s': FAILED - %s", model.getTenantID(), model.getRepositoryID(), gae.getMessage());
            return WebResponseBuilder.build(gae.getMessage(), Status.UNAUTHORIZED.getStatusCode());
    
        } catch (IOException ioe) {
            LOG.errorf("[%s] Syncing repository id '%s': FAILED - %s", model.getTenantID(), model.getRepositoryID(), ioe.getMessage());
            return WebResponseBuilder.build(ioe.getMessage(), Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
       return Response.status(Status.OK).build();
    
    }

    void cloneGitHubRepository(File checkoutDir, SyncRequestModel model) throws GitAPIException, IOException {
    
        LOG.infof("[%s] Syncing repository id '%s': cloning github repository", model.getTenantID(), model.getRepositoryID());

        if (checkoutDir.exists()) {
            FileUtils.deleteDirectory(checkoutDir);
        }

        AccessTokenResponseModel tokenResponse = (AccessTokenResponseModel) githubTokenService.getAccessToken(model.getTenantID()).getEntity();
        GitHubEntity githubEntity = (GitHubEntity) githubService.getRepositoryByID(model.getRepositoryID()).getEntity();

        Git gitrepo = Git.cloneRepository()
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(tokenResponse.getAccessToken(), ""))
            .setURI(githubEntity.repositoryURI)
            .setBranch(githubEntity.branch)
            .setDirectory(checkoutDir)
            .call();

        githubEntity.commitID = gitrepo.getRepository().getRefDatabase().findRef("HEAD").getObjectId().getName();

        LOG.infof("[%s] Syncing repository id '%s': clone succcessful on branch '%s' with commit id '%s' ", 
            model.getTenantID(), model.getRepositoryID(), githubEntity.branch, githubEntity.commitID);

        githubService.updateRepository(githubEntity);

        LOG.infof("[%s] Syncing repository id '%s': commit ID saved to entity", model.getTenantID());
    }
}