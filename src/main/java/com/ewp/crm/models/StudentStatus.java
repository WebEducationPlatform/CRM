package com.ewp.crm.models;

import javax.persistence.*;

/**
 * Специальность (направление обучения), Java Core, Java Web и тд. (Статус???)
 */
@Entity
@Table (name = "student_status")
public class StudentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudentStatus that = (StudentStatus) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StudentStatus{" + "id=" + id + ", status='" + status + "'}";
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
