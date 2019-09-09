package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table(name = "user_status")
public class UserStatus {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "status_id")
    private Long status_id;

    @Column(name = "is_invisible")
    private Boolean isInvisible = false;

    @Column(name = "position")
    private Long position;

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

    public Boolean getInvisible() {
        return isInvisible;
    }

    public void setInvisible(Boolean invisible) {
        isInvisible = invisible;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }
}
