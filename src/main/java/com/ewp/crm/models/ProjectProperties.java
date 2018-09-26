package com.ewp.crm.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//TODO Дополнить при необходимости полями для системных настроек
@Entity
@Table(name = "project_properties")
public class ProjectProperties {

    @Id
    @Column(name = "id")
    private Long id = 1L;

    @Column(name = "technical_account_token")
    private String technicalAccountToken;

    //ID статуса по умолчанию для клиентов (еще не студентов) вошедших в слак
    @Column(name = "default_status")
    private Long defaultStatusId;

    public ProjectProperties() {
    }

    public ProjectProperties(String technicalAccountToken) {
        this.technicalAccountToken = technicalAccountToken;
    }

    public ProjectProperties(Long id, String technicalAccountToken) {
        this.id = id;
        this.technicalAccountToken = technicalAccountToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTechnicalAccountToken() {
        return technicalAccountToken;
    }

    public void setTechnicalAccountToken(String technicalAccountToken) {
        this.technicalAccountToken = technicalAccountToken;
    }

    public Long getDefaultStatusId() {
        return defaultStatusId;
    }

    public void setDefaultStatusId(Long defaultStatusId) {
        this.defaultStatusId = defaultStatusId;
    }
}
