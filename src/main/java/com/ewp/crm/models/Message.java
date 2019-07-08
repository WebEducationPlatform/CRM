package com.ewp.crm.models;



import javax.persistence.*;

@Entity
@Table(name = "message")
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Long id;

	@Lob
	@Column(name = "content")
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private Type type;

	@Column(name = "author")
	private String authorName;

	public Message() {
	}

	public Message(Type type, String content) {
		this.type = type;
		this.content = content;
	}

	public Message(Type type, String content, String authorName) {
		this.content = content;
		this.type = type;
		this.authorName = authorName;
	}

	public String getContent() {
		return content;
	}

	public Type getType() {
		return type;
	}

	public Long getId() {
		return id;
	}

	public String getAuthorName() {
		return authorName;
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
