package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "social_network")
public class SocialProfile implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

/*	@Column(name = "link")
	private String link;*/

	@Column(name = "socialNetworkId")
	private long socialNetworkId;

	@ManyToOne
	@JoinTable(name = "social_network_social_network_type",
			joinColumns = {@JoinColumn(name = "social_network_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK_SOCIAL_NETWORK_TYPE"))},
			inverseJoinColumns = {@JoinColumn(name = "social_network_type_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK"))})
	private SocialProfileType socialProfileType;

	public SocialProfile() {
	}

	public SocialProfile(long socialNetworkId, SocialProfileType socialProfileType) {
		this.socialNetworkId = socialNetworkId;
		this.socialProfileType = socialProfileType;
	}

	public SocialProfile(long socialNetworkId) {
		this.socialNetworkId = socialNetworkId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SocialProfile)) return false;
		SocialProfile that = (SocialProfile) o;
		return id == that.id &&
				Objects.equals(socialNetworkId, that.socialNetworkId) &&
				Objects.equals(socialProfileType, that.socialProfileType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, socialNetworkId, socialProfileType);
	}

	@Override
	public String toString() {
		return String.valueOf(this.socialNetworkId);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/*public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}*/

	public long getSocialNetworkId() {
		return socialNetworkId;
	}

	public void setSocialNetworkId(long socialNetworkId) {
		this.socialNetworkId = socialNetworkId;
	}

	public SocialProfileType getSocialProfileType() {
		return socialProfileType;
	}

	public void setSocialProfileType(SocialProfileType socialProfileType) {
		this.socialProfileType = socialProfileType;
	}
}
