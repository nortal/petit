package com.nortal.petit.orm.statement.model;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "simple_table")
public class SimpleBean {

    @Id
    private Long id;
    private String description;
    
    
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
}
