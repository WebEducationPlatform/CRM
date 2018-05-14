package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.joda.time.DateTime;

import javax.persistence.*;

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

	//TODO потом переделать
	@Basic
	private String date = DateTime.now().toString("dd MMM 'в' HH:mm yyyy'г'");

	@Column(name = "history_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private Type type;

	//Use for generate title only
	@Transient
	private User user;

	//Use for generate title only
	@Transient
	private SocialNetworkType socialNetworkType;

	@OneToOne(cascade = CascadeType.ALL)
	private Message message;

	@JsonBackReference
	@ManyToOne
	@JoinTable(name = "history_client",
			joinColumns = {@JoinColumn(name = "history_id", foreignKey = @ForeignKey(name = "FK_HISTORY"))},
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))})
	private Client client;

	public ClientHistory() {
		this.type = Type.SYSTEM;
	}

	// Social actions
	public ClientHistory(SocialNetworkType socialNetworkType) {
		this.type = Type.SOCIAL_REQUEST;
		this.socialNetworkType = socialNetworkType;
	}

	// Worker actions
	public ClientHistory(Type type, User user) {
		this.type = type;
		this.user = user;
	}

	// Worker actions
	public ClientHistory(Type type, User user, String link) {
		this(type, user);
		this.link = link;
	}

	// Send Messages actions
	public ClientHistory(Type type, User user, Message message) {
		this(type, user);
		this.message = message;
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

	public Type getType() {
		return type;
	}

	public User getUser() {
		return user;
	}

	public Client getClient() {
		return client;
	}

	public String getDate() {
		return date;
	}

	public SocialNetworkType getSocialNetworkType() {
		return socialNetworkType;
	}

	public Message getMessage() {
		return message;
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
		SYSTEM("Клиент был добавлен при инициализации CRM"),
		ADD_CLIENT("добавил клиента вручную"),
		UPDATE_CLIENT("обновил информацию о клиенте"),
		STATUS("перевел клиента в статус:"),
		SMS("отправил смс"),
		CALL("позвонил клиенту"),
		POSTPONE("скрыл клиента до"),
		NOTIFICATION_POSTPONE("прочитал напоминание"),
		SOCIAL_REQUEST("клиент поступил из"),
		SEND_MESSAGE("отправил сообщение клиенту по"),
		DESCRIPTION("оставил комментарий на карточке");

		private String title;

		Type(String tittle) {
			this.title = tittle;
		}

		public String getTitle() {
			return title;
		}
	}
}
