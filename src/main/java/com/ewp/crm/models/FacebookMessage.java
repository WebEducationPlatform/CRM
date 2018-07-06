package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table(name = "facebook_message")
public class FacebookMessage {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Lob
	@Column(name = "text_message")
	private String textMessage;

	@Column(name = "sender")
	private String from;

	@Column(name = "receiver")
	private String to;

	@Column(name = "created_time")
	private String createdTime;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "message_dialog_fk")
	private MessageDialog messagesDialog;

	public FacebookMessage(String textMessage, String from, String to, String createdTime, MessageDialog messageDialog) {
		this.textMessage = textMessage;
		this.from = from;
		this.to = to;
		this.createdTime = createdTime;
		this.messagesDialog = messageDialog;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTextMessage() {
		return textMessage;
	}

	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public MessageDialog getMessageDialog() {
		return messagesDialog;
	}

	public void setMessageDialog(MessageDialog messageDialog) {
		this.messagesDialog = messageDialog;
	}
}
