package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Ссылка на профиль соцсети клиента (студента)
 */
@Entity
@Table(name = "social_network")
public class SocialProfile implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "social_id")
	private String socialId;

    /**
     * Тип соцсети
     */
	@ManyToOne
	@JoinTable(name = "social_network_social_network_type",
			joinColumns = {@JoinColumn(name = "social_network_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK_SOCIAL_NETWORK_TYPE"))},
			inverseJoinColumns = {@JoinColumn(name = "social_network_type_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK"))})
	private SocialProfileType socialProfileType;

	public SocialProfile() {
	}

	public SocialProfile(String socialId, SocialProfileType socialProfileType) {
        this.socialId = socialId;
		this.socialProfileType = socialProfileType;
	}

	public SocialProfile(String socialId) {
		this.socialId = socialId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SocialProfile)) return false;
		SocialProfile that = (SocialProfile) o;
		return id == that.id &&
				Objects.equals(socialId, that.socialId) &&
				Objects.equals(socialProfileType, that.socialProfileType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, socialId, socialProfileType);
	}

	@Override
	public String toString() {
		return String.valueOf(this.socialId);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public SocialProfileType getSocialProfileType() {
		return socialProfileType;
	}

	public void setSocialProfileType(SocialProfileType socialProfileType) {
		this.socialProfileType = socialProfileType;
	}
}
