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

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import dev.startupstack.codesyncservice.github.entity.GitHubEntity;

/**
 * GitSyncServiceImpl
 */
@ApplicationScoped
@Transactional(REQUIRED)
public class GitHubServiceImpl implements GitHubService {

	private static final Logger LOG = Logger.getLogger(GitHubServiceImpl.class);

	@Override
	@Transactional(SUPPORTS)
	public Response getRepositoryByID(Long id) {

		LOG.infof("Retrieving git repository ID %s ...", id);
		GitHubEntity entity = GitHubEntity.findById(id);

		if (entity == null) {
			LOG.warnf("Retrieving git repository ID %s: NOT FOUND", id);
			return Response.status(Status.NOT_FOUND).build();
		}

		LOG.infof("Retrieving git repository ID %s: OK", id);
		return Response.ok(entity).build();
	}

	@Override
	@Transactional(SUPPORTS)
	public Response getRepositoriesByTenantID(String tenantID) {
		LOG.infof("Retrieving git repositories for tenant '%s' ... ", tenantID);
		List<GitHubEntity> result = GitHubEntity.list("tenantid", tenantID);

		if (result.isEmpty()) {
			LOG.warnf("Retrieving git repositories for tenant '%s': NOT FOUND", tenantID);
			return Response.status(Status.NOT_FOUND).build();
		} 
		LOG.infof("Retrieving git repositories for tenant '%s': OK", tenantID);
		return Response.ok(result).build();
	}

	@Override
	public Response updateRepository(GitHubEntity updatedEntity) {

		LOG.infof("Updating git repository ID '%s' ...", updatedEntity.id);
		GitHubEntity entity = GitHubEntity.findById(updatedEntity.id);

		if (entity == null) {
			LOG.warnf("Updating repository ID '%s': NOT FOUND", updatedEntity.id);
			return Response.status(Status.NOT_FOUND).build();
		}

		entity.branch = updatedEntity.branch;
		entity.commitID = updatedEntity.commitID;
		entity.repositoryName = updatedEntity.repositoryName;
		entity.repositoryURI = updatedEntity.repositoryURI;
        entity.persist();
        
		LOG.infof("Updating git repository ID '%s': OK", updatedEntity.id);
		return Response.ok(entity).build();
	}

	@Override
	public Response deleteRepository(Long id) {
		LOG.infof("Deleting repository ID %s ...", id);
		GitHubEntity entity = GitHubEntity.findById(id);

		if (entity == null) {
			LOG.warnf("Deleting git repository ID %s: NOT FOUND", id);
			return Response.status(Status.NOT_FOUND).build();
		} 
		entity.delete();
		LOG.infof("Deleting git repository ID %s: OK", id);
		return Response.noContent().build();
	}

	@Override
	public Response createRepository(GitHubEntity entity) {
		LOG.infof("Creating new repository '%s' ...", entity.repositoryName);
		entity.persist();
		LOG.infof("Creating new repository: OK - Got ID '%s'", entity.id);
		return Response.status(Status.CREATED).build();
	}
}