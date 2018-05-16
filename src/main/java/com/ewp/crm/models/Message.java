package com.ewp.crm.models;

import javax.persistence.*;

@Entity
public class Message {

	@Id
	@GeneratedValue
	private long id;

	@Lob
	private String content;

	@Enumerated(EnumType.STRING)
	private Type type;

	public Message() {
	}

	public Message(Type type, String content) {
		this.type = type;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public Type getType() {
		return type;
	}

	public long getId() {
		return id;
	}

	public enum Type {
		VK("vk"),
		EMAIL("email"),
		FACEBOOK("facebook"),
		SMS("sms"),
		DATA();

		private String info;

		Type(){
		}

		Type(String info) {
			this.info = info;
		}

		public String getInfo() {
			return info;
		}
	}
}
