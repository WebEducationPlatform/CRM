package com.ewp.crm.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table
public class SocialNetwork {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column
    private String link;


    @OneToOne
    private SocialNetworkType socialNetworkType;

    public SocialNetwork() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocialNetwork)) return false;
        SocialNetwork that = (SocialNetwork) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(link, that.link) &&
                Objects.equals(socialNetworkType, that.socialNetworkType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, link, socialNetworkType);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public SocialNetworkType getSocialNetworkType() {
        return socialNetworkType;
    }

    public void setSocialNetworkType(SocialNetworkType socialNetworkType) {
        this.socialNetworkType = socialNetworkType;
    }
}
