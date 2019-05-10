package com.ewp.crm.models;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "call_client_info")
public class CallRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "record_link")
	private String link;

	@ManyToOne
	@JoinColumn(name = "client_client_id")
	private Client client;

	@OneToOne
	@JoinColumn(name = "client_history_history_id")
	private ClientHistory clientHistory;

	@Column(name = "comment")
	@Lob
	private String comment;

	@Column(name = "date")
	private ZonedDateTime date;

	@ManyToOne
	@JoinColumn(name = "calling_user_id")
	private User callingUser;

	public CallRecord() {}

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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public ClientHistory getClientHistory() {
		return clientHistory;
	}

	public void setClientHistory(ClientHistory clientHistory) {
		this.clientHistory = clientHistory;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ZonedDateTime getDate() {
		return date;
	}

	public void setDate(ZonedDateTime date) {
		this.date = date;
	}

	public User getCallingUser() {
		return callingUser;
	}

	public void setCallingUser(User callingUser) {
		this.callingUser = callingUser;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CallRecord)) return false;
		CallRecord that = (CallRecord) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(date, that.date) &&
				Objects.equals(link, that.link);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, date, link);
	}

	@Override
	public String toString() {
		return "CallRecord{" +
				"id=" + id +
				", link='" + link + '\'' +
				", client=" + client +
				", clientHistory=" + clientHistory +
				", comment='" + comment + '\'' +
				", date=" + date +
				'}';
	}
}
