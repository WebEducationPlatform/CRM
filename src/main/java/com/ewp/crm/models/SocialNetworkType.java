package com.ewp.crm.models;


import javax.persistence.*;
import java.util.Objects;

@Entity
public class SocialNetworkType {

    @Id
    @GeneratedValue
    @Column(name = "socialType_id")
    private Long id;

    @Column
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SocialNetworkType(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocialNetworkType)) return false;
        SocialNetworkType that = (SocialNetworkType) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
