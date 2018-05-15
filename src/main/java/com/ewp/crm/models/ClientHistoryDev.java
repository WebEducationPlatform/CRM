package com.ewp.crm.models;

import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
public class ClientHistoryDev {

	@Id
	@GeneratedValue
	@Column(name = "history_id")
	private long id;

	@Column(nullable = false)
	private String tittle;

	@Basic
	private String link;

	//TODO потом переделать
	@Basic
	private String date = DateTime.now().toString("dd MMM 'в' HH:mm yyyy'г'");

	@Column(name = "history_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private Type type;

	@OneToOne(cascade = CascadeType.ALL)
	private Message message;

	public ClientHistoryDev() {
		//TODO date init
	}

	public ClientHistoryDev(Type type) {
		super();
		this.type = type;
	}

	public enum Type {
		SOCIAL_REQUEST("Клиент был добавлен из"),
		STATUS("переместил клиента в статус:"),
		DESCRIPTION("добавил комментарий к клиенту"),
		POSTPONE("установил напоминание на"),
		NOTIFICATION("прочитал напоминание"),
		ASSIGN("прикрепил"),
		UNASSIGN("открепил"),
		CALL("позвонил"),
		SEND_MESSAGE("отправил сообщение по"),
		ADD("добаил вручную"),
		UPDATE("обновил информацию");

		private String info;

		Type(String info) {
			this.info = info;
		}
	}
}
