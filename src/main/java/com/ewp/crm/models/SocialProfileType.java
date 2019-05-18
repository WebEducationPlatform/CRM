package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Тип социальной сети (справочник)
 */
@Entity
@Table(name = "social_network_type") // зачем давайть разные имена таблице и классу-сущности??
public class SocialProfileType implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "link")
	private String link;

	/**
	 * Соцсеть клиента (студента)
	 */
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "socialProfileType")
	private List<SocialProfile> socialProfileList;

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

	public SocialProfileType() {
	}

	public SocialProfileType(String name, String link) {
		this.name = name;
		this.link = link;
	}

	public SocialProfileType(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SocialProfileType)) return false;
		SocialProfileType that = (SocialProfileType) o;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		return name != null ? name.equals(that.name) : that.name == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public List<SocialProfile> getSocialProfileList() {
		return socialProfileList;
	}

	public void setSocialProfileList(List<SocialProfile> socialProfileList) {
		this.socialProfileList = socialProfileList;
	}
}
