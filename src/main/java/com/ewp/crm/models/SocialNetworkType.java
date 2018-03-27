package com.ewp.crm.models;


import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "socialType")
public class SocialType {

    @Id
    @GeneratedValue
    @Column(name = "socialType_id")
    private Long id;

    @Column
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocialType)) return false;
        SocialType that = (SocialType) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
