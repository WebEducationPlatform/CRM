package com.ewp.crm.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class SocialNetworkType implements Serializable {

	@Id
	@GeneratedValue
	@Column
	private Long id;

	@Column
	private String name;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "social_network_social_network_type",
			inverseJoinColumns = {@JoinColumn(name = "social_network_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK_SOCIAL_NETWORK_TYPE"))},
			joinColumns = {@JoinColumn(name = "social_network_type_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK"))})
	private List<SocialNetwork> socialNetworkList;

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

	public SocialNetworkType() {
	}

	public SocialNetworkType(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SocialNetworkType)) return false;
		SocialNetworkType that = (SocialNetworkType) o;
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

	public List<SocialNetwork> getSocialNetworkList() {
		return socialNetworkList;
	}

	public void setSocialNetworkList(List<SocialNetwork> socialNetworkList) {
		this.socialNetworkList = socialNetworkList;
	}
}
