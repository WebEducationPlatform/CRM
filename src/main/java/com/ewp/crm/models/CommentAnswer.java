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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))
	private User user;

	@Column(name = "date")
	private ZonedDateTime dateFormat; // имя переменной вводит в заблуждение

	@Column(name = "content")
	@Lob
	private String content;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "COMMENT_FK"))
	private Comment originalComment;

	public CommentAnswer() {

	}

	public CommentAnswer(User user, String content, Comment originalComment) {
		this.user = user;
		this.content = content;
		this.originalComment = originalComment;
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

	public Comment getOriginalComment() {
		return originalComment;
	}

	public void setOriginalComment(Comment originalComment) {
		this.originalComment = originalComment;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ZonedDateTime getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(ZonedDateTime dateFormat) {
		this.dateFormat = dateFormat;
	}

}
