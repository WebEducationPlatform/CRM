package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "job")
public class Job implements Serializable {

    @Id
    @Column(name = "job_id")
    @GeneratedValue
    private Long id;

    @Column
    private String organization;

    @Column
    private String position;

    public Job() {
    }

    public Job(String organization, String position) {
        this.organization = organization;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job)) return false;

        Job job = (Job) o;

        if (!id.equals(job.id)) return false;
        if (!organization.equals(job.organization)) return false;
        return position.equals(job.position);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + organization.hashCode();
        result = 31 * result + position.hashCode();
        return result;
    }
}
