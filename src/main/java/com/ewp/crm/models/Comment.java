package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "comment")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long id;

	/**
	 * ManyToOne uses FetchType.EAGER by default.
	 */
	@ManyToOne
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))
	private User user;

	/**
	 * We use FetchType.LAZY for lazy initialization.
	 */
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "client_comment",
			joinColumns = {@JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "FK_COMMENT_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_COMMENT"))})
	private Client client;

	@Column(name = "date")
	private ZonedDateTime dateFormat;


	@Column(name = "content")
	@Lob
	private String content;

	/**
	 * We use FetchType.LAZY for lazy initialization.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "originalComment")
//	@OrderBy("date DESC")
//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	@JoinTable(name = "comment_comment_answer",
//			joinColumns = {@JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "FK_COMMENT_ANSWER"))},
//			inverseJoinColumns = {@JoinColumn(name = "answer_id", foreignKey = @ForeignKey(name = "FK_ANSWER"))})
	private List<CommentAnswer> commentAnswers;

	public Comment() {
	}


	public Comment(User user, Client client, String content) {
		this.user = user;
		this.client = client;
		this.content = content;
		setDateFormat(ZonedDateTime.now());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ZonedDateTime getDateFormat() {
		return dateFormat;
	}

	private void setDateFormat(ZonedDateTime dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Comment comment = (Comment) o;
		if (!Objects.equals(id, comment.id)) return false;
		if (!Objects.equals(user, comment.user)) return false;
		if (!Objects.equals(client, comment.client)) return false;
		if (!Objects.equals(dateFormat, comment.dateFormat)) return false;
		return Objects.equals(content, comment.content);
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (user != null ? user.hashCode() : 0);
		result = 31 * result + (client != null ? client.hashCode() : 0);
		result = 31 * result + (dateFormat != null ? dateFormat.hashCode() : 0);
		result = 31 * result + (content != null ? content.hashCode() : 0);
		return result;
	}

	public List<CommentAnswer> getCommentAnswers() {
		return commentAnswers;
	}

	//TODO проверить используется ли метод и удалить если нет
	public void setCommentAnswers(List<CommentAnswer> commentAnswers) {
		this.commentAnswers = commentAnswers;
	}

	public void addAnswer(CommentAnswer commentAnswer) {
		this.commentAnswers.add(commentAnswer);
	}
}
