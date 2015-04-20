package com.nortal.petit.orm.statement.model;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "simple_string_table")
public class SimpleStringBean {

    @Id
    private String kood;
    private String description;
    
    
    public String getKood() {
        return kood;
    }
    public void setKood(String kood) {
        this.kood = kood;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
