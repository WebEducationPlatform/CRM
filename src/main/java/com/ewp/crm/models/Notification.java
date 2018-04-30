package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "notifications")
public class Notification implements Serializable {

	@Id
	@GeneratedValue
	@Column
	private Long id;

	@Basic
	private String information;

	@ManyToOne
	@JoinTable(name = "client_notification",
			joinColumns = {@JoinColumn(name = "notification_id", foreignKey = @ForeignKey(name = "FK_NOTIFICATION_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_NOTIFICATION"))})
	@JsonIgnore
	private Client client;

	@ManyToOne
	@JoinTable(name = "user_notification",
			joinColumns = {@JoinColumn(name = "notification_id", foreignKey = @ForeignKey(name = "FK_NOTIFICATION_USER"))},
			inverseJoinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_NOTIFICATION"))})
	@JsonIgnore
	private User userToNotify;

	@Enumerated(EnumType.STRING)
	private Type type;

	public Notification() {
	}

	public Notification(Client client, User userToNotify, Type type) {
		this.client = client;
		this.userToNotify = userToNotify;
		this.type = type;
	}

	public Notification(String information, Client client, User userToNotify, Type type) {
		this.information = information;
		this.client = client;
		this.userToNotify = userToNotify;
		this.type = type;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUserToNotify() {
		return userToNotify;
	}

	public void setUserToNotify(User userToNotify) {
		this.userToNotify = userToNotify;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Notification)) return false;
		Notification that = (Notification) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(client, that.client) &&
				Objects.equals(userToNotify, that.userToNotify);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, client, userToNotify);
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public enum Type{
		COMMENT, SMS
	}
}
