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

package dev.startupstack.codesyncservice.github;

import static dev.startupstack.codesyncservice.Constants.GIT_URL;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import dev.startupstack.codesyncservice.github.entity.GitHubEntity;

@RequestScoped
@Path(GIT_URL)
@Tag(name = "git")
//@RolesAllowed({ROLE_TENANT_ADMIN, ROLE_TENANT_USER})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GitHubResource {

    @Inject
    GitHubService gitService;

    @POST
    @Operation(summary = "Registers a new git repository for the code sync service")
    @APIResponse(responseCode = "201", description = "Git repository created" )
    @APIResponse(responseCode = "401", description = "No valid JWT token found")
    @APIResponse(responseCode = "403", description = "Not authorized to query this repository")
    public Response createRepository(@Valid GitHubEntity entity) {
        return gitService.createRepository(entity);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Gets an existing git repository by internal id")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GitHubEntity.class)))
    @APIResponse(responseCode = "401", description = "No valid JWT token found")
    @APIResponse(responseCode = "403", description = "Not authorized to query this repository")
    @APIResponse(responseCode = "404", description = "No record found with given ID" )
    public Response getRepositoryByID(@PathParam("id") Long id) {
        return gitService.getRepositoryByID(id);
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Updates a repository with new values")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GitHubEntity.class)))
    @APIResponse(responseCode = "401", description = "No valid JWT token found")
    @APIResponse(responseCode = "403", description = "Not authorized to query this repository")
    @APIResponse(responseCode = "404", description = "Repository not found" )
    public Response updateRepository (@Valid GitHubEntity entity) {
        return gitService.updateRepository(entity);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes a repository")
    @APIResponse(responseCode = "204", description = "Repository successfully deleted")
    @APIResponse(responseCode = "401", description = "No valid JWT token found")
    @APIResponse(responseCode = "403", description = "Not authorized to query this repository")
    @APIResponse(responseCode = "404", description = "Repository not found" )
    public Response deleteRepository (@PathParam("id") Long id)  {
        return gitService.deleteRepository(id);
    }

    @GET
    @Path("/tenant/{id}")
    @Operation(summary = "Gets all existing repositories by tenant ID")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GitHubEntity.class)))
    @APIResponse(responseCode = "401", description = "No valid JWT token found")
    @APIResponse(responseCode = "403", description = "Not authorized to query this repository")
    @APIResponse(responseCode = "404", description = "No records found for this tenant ID" )
    public Response getRepositoriesByTenantID(@PathParam("id") String id) {
        return gitService.getRepositoriesByTenantID(id);
    }

}