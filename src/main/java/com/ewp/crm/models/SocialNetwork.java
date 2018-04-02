package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table
public class SocialNetwork implements Serializable {

	@Id
	@GeneratedValue
	@Column
	private Long id;

	@Column
	private String link;

	@ManyToOne
	@JoinTable(name = "client_social_network",
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
			joinColumns = {@JoinColumn(name = "social_network_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK"))})
	private Client client;


	@OneToOne
	private SocialNetworkType socialNetworkType;

	public SocialNetwork() {
	}

	public SocialNetwork(String link) {
		this.link = link;
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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
}
