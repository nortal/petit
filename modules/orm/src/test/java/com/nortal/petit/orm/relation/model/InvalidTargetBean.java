/**
 *   Copyright 2014 Nortal AS
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.nortal.petit.orm.relation.model;

import java.util.List;

import javax.persistence.Id;

/**
 * Invalid target bean. Relations setter does not follow common setter pattern
 * 
 * @author Alrik Peets
 */
public class InvalidTargetBean {

	@Id
	public Long id;
	private String description;
	private List<RelationBean> relations;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<RelationBean> getRelations() {
		return relations;
	}

	/**
	 * We add only active relations
	 * 
	 * @param relations
	 * @param activeOnly
	 */
	public void setRelations(List<RelationBean> relations, boolean activeOnly) {
		this.relations = relations;
	}

}
