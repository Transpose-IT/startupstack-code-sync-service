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

package dev.startupstack.codesyncservice.github.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * GitEntity
 */
@Entity
public class GitHubEntity extends PanacheEntity  {

    @NotBlank
    public String tenantID;

    @NotBlank
    public String repositoryName;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    public String repositoryURI;

    @NotBlank
    public String branch;

    @NotBlank
    public String commitID;


    @Override
    public String toString() {
        return "{" +
            " tenantID='" + tenantID + "'" +
            ", repositoryURL='" + repositoryURI + "'" +
            ", branch='" + branch + "'" +
            ", commitID='" + commitID + "'" +
            "}";
    }


    
}