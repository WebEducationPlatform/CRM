package com.ewp.crm.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "message_dialog")
public class MessageDialog {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "dialog_id")
	private String dialogId;

	@OneToMany(mappedBy = "messagesDialog")
	private List<FacebookMessage> messages = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDialogId() {
		return dialogId;
	}

	public void setDialogId(String dialogId) {
		this.dialogId = dialogId;
	}

	public List<FacebookMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<FacebookMessage> messages) {
		this.messages = messages;
	}
}
