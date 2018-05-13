package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "history")
public class ClientHistory {

	@Id
	@GeneratedValue
	@Column(name = "history_id")
	private Long id;

	@Column(nullable = false)
	private String title;

	@Basic
	private String link;

	@Basic
	private Date date = new Date();

	@Column(name = "history_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private Type type;

	//Use for generate title only
	@Transient
	private User user;

	//Use for generate title only
	@Transient
	private SocialNetworkType socialNetworkType;

	@JsonBackReference
	@ManyToOne
	@JoinTable(name = "history_client",
			joinColumns = {@JoinColumn(name = "history_id", foreignKey = @ForeignKey(name = "FK_HISTORY"))},
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))})
	private Client client;

	public ClientHistory() {
	}

	//TODO REFACTORING - delete this constructor
	public ClientHistory(String title) {
		this.title = title;
	}

	// System actions
	public ClientHistory(Type type) {
		this.type = type;
	}

	// Social actions
	public ClientHistory(Type type, SocialNetworkType socialNetworkType) {
		this.type = type;
		this.socialNetworkType = socialNetworkType;
	}

	// Worker actions
	public ClientHistory(Type type, User user) {
		this.type = type;
		this.user = user;
	}

	// Worker actions
	public ClientHistory(Type type, User user, String link) {
		this.type = type;
		this.link = link;
		this.user = user;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public Date getDate() {
		return date;
	}

	public Type getType() {
		return type;
	}

	public User getUser() {
		return user;
	}

	public Client getClient() {
		return client;
	}

	public SocialNetworkType getSocialNetworkType() {
		return socialNetworkType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClientHistory)) return false;

		ClientHistory that = (ClientHistory) o;

		if (!id.equals(that.id)) return false;
		if (!title.equals(that.title)) return false;
		return client.equals(that.client);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + title.hashCode();
		result = 31 * result + client.hashCode();
		return result;
	}


	public enum Type {
		SYSTEM,
		ADD_CLIENT,
		UPDATE_CLIENT,
		STATUS,
		SMS,
		CALL,
		POSTPONE,
		SOCIAL_REQUEST
	}
}
