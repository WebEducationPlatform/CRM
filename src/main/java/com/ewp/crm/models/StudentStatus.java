package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table (name = "student_status")
public class StudentStatus {

    @Id
    @GeneratedValue
    @Column (name = "id")
    private Long id;

    @Column (name = "status")
    private String status;

    public StudentStatus() {
    }

    public StudentStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StudentStatus{" + "id=" + id + ", status='" + status + '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
