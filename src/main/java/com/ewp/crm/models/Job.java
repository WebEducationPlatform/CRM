package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table(name = "job")
public class Job {

    @Id
    @Column(name = "job_id")
    @GeneratedValue
    private Long id;

    @Column(name = "organization")
    private String organization;

    @Column(name = "position")
    private String position;

    @OneToOne(mappedBy = "job")
    private Client client;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        if (!id.equals(job.id)) return false;
        return client.equals(job.client);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + client.hashCode();
        return result;
    }
}
