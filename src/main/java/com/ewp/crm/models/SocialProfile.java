package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
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

	@Enumerated(EnumType.STRING)
	@Column(name = "social_network_type")
	private SocialNetworkType socialNetworkType;

	public SocialProfile() {
	}

	public SocialProfile(String socialId, SocialNetworkType socialNetworkType) {
		this.socialId = socialId;
		this.socialNetworkType = socialNetworkType;
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
				Objects.equals(socialNetworkType, that.socialNetworkType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, socialId, socialNetworkType);
	}

	@Override
	public String toString() {
		return String.format("{type = '%s', socialId = '%s'}",
				Objects.isNull(socialNetworkType) ? SocialNetworkType.UNKNOWN.getName() : socialNetworkType.getName(), socialId);
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

	public SocialNetworkType getSocialNetworkType() {
		return socialNetworkType;
	}

	public void setSocialNetworkType(SocialNetworkType socialNetworkType) {
		this.socialNetworkType = socialNetworkType;
	}

	public List<SocialNetworkType> getAllSocialNetworkTypes(){
		return new ArrayList<>(EnumSet.allOf(SocialNetworkType.class));
	}

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
	public enum SocialNetworkType {

		VK(1L,"vk","https://vk.com/id"),
		FACEBOOK(2L,"facebook","https://www.facebook.com/"),
		UNKNOWN(3L,"unknown", ""),
		TELEGRAM(4L,"telegram", ""),
		WHATSAPP(5L,"whatsapp", ""),
		SLACK(6L,"slack", "");

		private String name;
		private Long id;
		private String link;

		SocialNetworkType(){
		}

		SocialNetworkType(Long id, String name, String link) {
			this.id = id;
			this.name = name;
			this.link = link;
		}
        @JsonProperty("id")
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
        @JsonProperty("name")
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
        @JsonProperty("link")
		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}
	}
}
