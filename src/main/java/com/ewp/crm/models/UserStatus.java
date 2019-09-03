package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table(name = "user_status")
public class UserStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "status_id")
    private Long status_id;

    @Column(name = "status_position")
    private Long status_position;

    @Column(name = "status_visible")
    private Boolean status_visible = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getStatus_id() {
        return status_id;
    }

    public void setStatus_id(Long status_id) {
        this.status_id = status_id;
    }

    public Long getStatus_position() {
        return status_position;
    }

    public void setStatus_position(Long status_position) {
        this.status_position = status_position;
    }

    public Boolean getStatus_visible() {
        return status_visible;
    }

    public void setStatus_visible(Boolean status_visible) {
        this.status_visible = status_visible;
    }
}
