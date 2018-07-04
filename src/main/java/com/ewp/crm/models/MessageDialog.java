package com.ewp.crm.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Entity
public class MessageDialog {


//	Диалог который ииеет id
//  Содержит в себе лист сообщений диалога
//	Каждое оообщение имеет всремя создания, и собственно текст
//


	@Id
	@GeneratedValue
	@Column(name = "dialog_id")
	private long dialogId;

	@Column(name = "from")
	String from;

	@Column(name = "to")
	String to;

	@Column(name = "created_time")
	long createdTime;

	@Column(name = "dialog_messages")
	List<String> dialogMessages;


}
