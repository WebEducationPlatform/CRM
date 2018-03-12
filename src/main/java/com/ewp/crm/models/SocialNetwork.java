package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table
public class SocialNetwork {

	@Id
	@GeneratedValue
	@Column
	private Long id;

	@Column
	private String link;

	@Column
	@Enumerated(EnumType.STRING)
	private SocialMarker socialMarker;

	public SocialNetwork() {
	}

	public SocialNetwork(String link, SocialMarker socialMarker) {
		this.link = link;
		this.socialMarker = socialMarker;
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

	public SocialMarker getSocialMarker() {
		return socialMarker;
	}

	public void setSocialMarker(SocialMarker socialMarker) {
		this.socialMarker = socialMarker;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SocialNetwork that = (SocialNetwork) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (link != null ? !link.equals(that.link) : that.link != null) return false;
		return socialMarker == that.socialMarker;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (link != null ? link.hashCode() : 0);
		result = 31 * result + (socialMarker != null ? socialMarker.hashCode() : 0);
		return result;
	}

	public enum SocialMarker {
		FACEBOOK, VK
	}
}
