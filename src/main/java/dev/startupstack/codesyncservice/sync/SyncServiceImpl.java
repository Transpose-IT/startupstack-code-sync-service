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
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import dev.startupstack.codesyncservice.utils.WebResponseBuilder;


import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import dev.startupstack.codesyncservice.utils.ArchiveUtils;

/**
 * GitSyncServiceImpl
 */
@Dependent
public class SyncServiceImpl implements SyncService {

    @Override
    public Response syncRepository(String name) {

        File checkoutDir = new File("/home/jorn/checkout");

        try {
            cloneRepository(checkoutDir);
            ArchiveUtils archiveUtils = new ArchiveUtils();
            archiveUtils.createTarGZ("test.tar.gz", checkoutDir);

        } catch (GitAPIException gae) {
            return WebResponseBuilder.build(gae.getMessage(), Status.UNAUTHORIZED.getStatusCode());
    
        } catch (IOException ioe) {
            return WebResponseBuilder.build(ioe.getMessage(), Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
       return Response.status(Status.OK).build();
    
    }

    void cloneRepository(File checkoutDir) throws GitAPIException, IOException {
    
        // if (checkoutDir.exists()) {
        //     FileUtils.deleteDirectory(checkoutDir);
        // }

        // Git.cloneRepository()
        // .setCredentialsProvider(new UsernamePasswordCredentialsProvider("<token>", ""))
        // .setURI("https://github.com/jargelo/test-private-repo")
        // .setBranch("master")
        // .setDirectory(checkoutDir)
        // .call();

    }
}