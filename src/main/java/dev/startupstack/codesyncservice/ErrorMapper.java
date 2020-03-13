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
package dev.startupstack.codesyncservice;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.core.NoMessageBodyWriterFoundFailure;

import dev.startupstack.codesyncservice.utils.WebResponseBuilder;
import dev.startupstack.codesyncservice.utils.WebResponseModel;
import io.vertx.core.http.HttpServerRequest;

/**
 * This intercepts the exceptions thrown and formats it according to the
 * {@link WebResponseModel} so that we always get properly formatted JSON back
 */
@Provider
public class ErrorMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(ExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Context
    HttpServerRequest request;

    @Override
    public Response toResponse(Exception exception) {
        int code = 500;
        if (exception instanceof WebApplicationException) {
            code = ((WebApplicationException) exception).getResponse().getStatus();
        }

        if (exception instanceof NoMessageBodyWriterFoundFailure) {
            LOG.error(exception.getMessage());
            return WebResponseBuilder.build("Unable to marshal / unmarshal return data", 500);
        }
        else if (exception instanceof NotAllowedException) {
            LOG.warnf("%s %s: FAILED - %s", request.method(), uriInfo.getAbsolutePath(), Status.METHOD_NOT_ALLOWED.getStatusCode());

            return WebResponseBuilder.build("Method not allowed", Status.METHOD_NOT_ALLOWED.getStatusCode());
        }
        else if (exception instanceof ForbiddenException) {
            LOG.warnf("%s %s: FAILED - %s", request.method(), uriInfo.getAbsolutePath(), Status.FORBIDDEN.getStatusCode());

            return WebResponseBuilder.build("Method not allowed", Status.FORBIDDEN.getStatusCode());
        }
        else {
            LOG.error(exception.getMessage(), exception);
            return Response.status(code).entity(new WebResponseModel(exception.getMessage(), code)).build();    
        }
    }
}