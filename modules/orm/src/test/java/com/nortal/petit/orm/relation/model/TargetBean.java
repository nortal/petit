package com.nortal.petit.orm.relation;

import java.util.List;

import javax.persistence.Id;

/**
 * Test-class representing target bean in RelationMapper tests
 * 
 * @author Alrik Peets
 *
 */
public class TargetBean {
	@Id
	private Long id;
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

	public void setRelations(List<RelationBean> relations) {
		this.relations = relations;
	}
}
