package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;


import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "comment_answer")
public class CommentAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "answer_id")
	private Long id;

	/**
	 * We use FetchType.LAZY for lazy initialization.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))
	private User user;

	/**
	 * We use FetchType.LAZY for lazy initialization.
	 */
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "client_comment_answer",
			joinColumns = {@JoinColumn(name = "answer_id", foreignKey = @ForeignKey(name = "FK_COMMENT_ANSWER_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_COMMENT_ANSWER"))})
	private Client client;

	@Column(name = "date")
	private ZonedDateTime dateFormat;

	@Column(name = "content")
	@Lob
	private String content;

	/**
	 * We use FetchType.LAZY for lazy initialization.
	 */
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "comment_comment_answer",
			joinColumns = {@JoinColumn(name = "answer_id", foreignKey = @ForeignKey(name = "FK_COMMENT_ANSWER"))},
			inverseJoinColumns = {@JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "FK_ANSWER"))})
	private Comment mainComment;


	public CommentAnswer() {
	}

	public CommentAnswer(User user, String content, Client client) {
		this.user = user;
		this.content = content;
		this.client = client;
		setDateFormat(ZonedDateTime.now());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Comment getMainComment() {
		return mainComment;
	}

	public void setMainComment(Comment mainComment) {
		this.mainComment = mainComment;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public ZonedDateTime getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(ZonedDateTime dateFormat) {
		this.dateFormat = dateFormat;
	}
}
