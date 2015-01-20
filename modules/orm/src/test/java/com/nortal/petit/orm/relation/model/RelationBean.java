package com.nortal.petit.orm.relation;

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
