package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Objects;

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
	@Lob
	private String link;

	//TODO потом переделать
	@Basic
	private String date = DateTime.now().toString("HH:mm ddMMM yyyy'г'");

	@Column(name = "history_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private Type type;

	@OneToOne(cascade = CascadeType.ALL)
	private Message message;

	@JsonBackReference
	@ManyToOne
	@JoinTable(name = "history_client",
			joinColumns = {@JoinColumn(name = "history_id", foreignKey = @ForeignKey(name = "FK_HISTORY"))},
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))})
	private Client client;

	public ClientHistory() {
		//TODO date init
	}

	public ClientHistory(Type type) {
		this();
		this.type = type;
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

	public void setMessage(Message message) {
		this.message = message;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDate() {
		return date;
	}

	public Type getType() {
		return type;
	}

	public Message getMessage() {
		return message;
	}

	public Client getClient() {
		return client;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClientHistory)) return false;
		ClientHistory that = (ClientHistory) o;
		return Objects.equals(id,that.id) &&
				Objects.equals(date, that.date);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, date);
	}

	public enum Type {
		SOCIAL_REQUEST("Клиент был добавлен из"),
		STATUS("переместил клиента в статус:"),
		DESCRIPTION("добавил комментарий к клиенту:"),
		POSTPONE("установил напоминание на"),
		NOTIFICATION("прочитал напоминание"),
		ASSIGN("прикрепил"),
		UNASSIGN("открепил"),
		CALL("совершил звонок"),
		SEND_MESSAGE("отправил сообщение по"),
		ADD("добавил вручную"),
		UPDATE("обновил информацию");

		private String info;

		Type(String info) {
			this.info = info;
		}

		public String getInfo() {
			return info;
		}
	}
}
