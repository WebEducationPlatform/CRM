package com.ewp.crm.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class PostponeClientData {

	@Id
	@GeneratedValue
	private Long id;

	private Date postponeDate;

	public PostponeClientData() {
	}

	private String comment;

	@OneToOne(mappedBy = "postponeClientData")
	private Client client;

	public PostponeClientData(Date postponeDate, String comment) {
		this.postponeDate = postponeDate;
		this.comment = comment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getPostponeDate() {
		return postponeDate;
	}

	public void setPostponeDate(Date postponeDate) {
		this.postponeDate = postponeDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
}
