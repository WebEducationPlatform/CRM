package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "job")
public class Job implements Serializable {

    @Id
    @Column(name = "job_id")
    @GeneratedValue
    private long id;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
		return id == job.id &&
				Objects.equals(organization, job.organization) &&
				Objects.equals(position, job.position);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, organization, position);
	}

	@Override
	public String toString() {
		return this.organization + " " + this.position;
	}
}
