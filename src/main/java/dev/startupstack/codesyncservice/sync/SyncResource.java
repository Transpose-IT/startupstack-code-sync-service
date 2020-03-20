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

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import dev.startupstack.codesyncservice.sync.models.SyncRequestModel;
import dev.startupstack.codesyncservice.utils.WebResponseBuilder;

import static dev.startupstack.codesyncservice.Constants.*;

@RequestScoped
@Path(SYNC_URL)
@Tag(name = "sync")
//@RolesAllowed({ROLE_TENANT_ADMIN, ROLE_TENANT_USER})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SyncResource {

    @Inject
    SyncService syncService;

    @POST
    @Operation(summary = "Trigger a sync of a given git repository")
    //@APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ObjectInfoModel.class)))
    @APIResponse(responseCode = "401", description = "No valid JWT token found")
    @APIResponse(responseCode = "403", description = "Not authorized for this git repository")
    @APIResponse(responseCode = "404", description = "Git repository not found (it must be registered first with the /git API)")
    public Response getObjectInfo(@Valid SyncRequestModel model) {
         return syncService.syncRepository(model);
    }
}