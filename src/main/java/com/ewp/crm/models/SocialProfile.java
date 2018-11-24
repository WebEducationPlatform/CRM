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

	@Column(name = "link")  // ссылка на профиль, по которой и так понятно, что это за соцсеть. зачем связанная таблица?
	private String link;

	@ManyToOne // зачем??
	@JoinTable(name = "social_network_social_network_type",
			joinColumns = {@JoinColumn(name = "social_network_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK_SOCIAL_NETWORK_TYPE"))},
			inverseJoinColumns = {@JoinColumn(name = "social_network_type_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK"))})
	private SocialProfileType socialProfileType;

	public SocialProfile() {
	}

	public SocialProfile(String link, SocialProfileType socialProfileType) {
		this.link = link;
		this.socialProfileType = socialProfileType;
	}

	public SocialProfile(String link) {
		this.link = link;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SocialProfile)) return false;
		SocialProfile that = (SocialProfile) o;
		return id == that.id &&
				Objects.equals(link, that.link) &&
				Objects.equals(socialProfileType, that.socialProfileType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, link, socialProfileType);
	}

	@Override
	public String toString() {
		return this.link;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public SocialProfileType getSocialProfileType() {
		return socialProfileType;
	}

	public void setSocialProfileType(SocialProfileType socialProfileType) {
		this.socialProfileType = socialProfileType;
	}
}
