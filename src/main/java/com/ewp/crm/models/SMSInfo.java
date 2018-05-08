package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sms_info")
public class SMSInfo implements Serializable {

	private static final long serialVersionUID = -1897381471234461980L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "sms_id")
	private long smsId;

	@Basic
	private String message;

	@Basic
	private Boolean isDelivered = false;

	@ManyToOne
	@JoinTable(name = "client_sms_info",
			joinColumns = {@JoinColumn(name = "sms_info_id", foreignKey = @ForeignKey(name = "FK_SMS_INFO"))},
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))})
	private Client client;

	@ManyToOne
	@JoinTable(name = "worker_send_sms",
			joinColumns = {@JoinColumn(name = "sms_info_id", foreignKey = @ForeignKey(name = "FK_SMS_INFO"))},
			inverseJoinColumns = {@JoinColumn(name = "worker_id", foreignKey = @ForeignKey(name = "FK_USER"))})
	private User user;

	public SMSInfo() {
	}

	public SMSInfo(long smsId, String message, User whoSend) {
		this.smsId = smsId;
		this.message = message;
		this.user = whoSend;
	}

	public Boolean getDelivered() {
		return isDelivered;
	}

	public void setDelivered(Boolean delivered) {
		isDelivered = delivered;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getSmsId() {
		return smsId;
	}

	public void setSmsId(long smsId) {
		this.smsId = smsId;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
