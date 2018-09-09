package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "social_network")
public class SocialNetwork implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "link")
	private String link;

	@JsonIgnore
	@ManyToOne
	@JoinTable(name = "client_social_network",
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
			joinColumns = {@JoinColumn(name = "social_network_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK"))})
	private Client client;


	@ManyToOne
	@JoinTable(name = "social_network_social_network_type",
			joinColumns = {@JoinColumn(name = "social_network_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK_SOCIAL_NETWORK_TYPE"))},
			inverseJoinColumns = {@JoinColumn(name = "social_network_type_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK"))})
	private SocialNetworkType socialNetworkType;

	public SocialNetwork() {
	}

	public SocialNetwork(String link, SocialNetworkType socialNetworkType) {
		this.link = link;
		this.socialNetworkType = socialNetworkType;
	}

	public SocialNetwork(String link) {
		this.link = link;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SocialNetwork)) return false;
		SocialNetwork that = (SocialNetwork) o;
		return id == that.id &&
				Objects.equals(link, that.link) &&
				Objects.equals(socialNetworkType, that.socialNetworkType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, link, socialNetworkType);
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

	public SocialNetworkType getSocialNetworkType() {
		return socialNetworkType;
	}

	public void setSocialNetworkType(SocialNetworkType socialNetworkType) {
		this.socialNetworkType = socialNetworkType;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
}
