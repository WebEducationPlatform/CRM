package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table (name = "student_staus")
public class StudentStatus {

    @Id
    @GeneratedValue
    private Long id;

    @Column (name = "status")
    private String status;

    public StudentStatus() {
    }

    public StudentStatus(String status) {
        this.status = status;
    }
}
