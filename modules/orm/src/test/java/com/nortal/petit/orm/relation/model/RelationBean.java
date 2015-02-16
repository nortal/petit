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

import javax.persistence.Id;

/**
 * Bean representing relations in RelationMapper tests
 * 
 * @author Alrik Peets
 */
public class RelationBean {

	@Id
	private Long id;
	private Long targetBeanId;
	private String relationDescription;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTargetBeanId() {
		return targetBeanId;
	}

	public void setTargetBeanId(Long targetBeanId) {
		this.targetBeanId = targetBeanId;
	}

	public String getRelationDescription() {
		return relationDescription;
	}

	public void setRelationDescription(String relationDescription) {
		this.relationDescription = relationDescription;
	}

}
