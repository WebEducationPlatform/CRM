package com.ewp.crm.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "skype_call")
public class SkypeCall {

	@Id
	@GeneratedValue
	@Column(name = "skype_call_id")
	private Long id;

	@Column(name = "skype_login")
	private String login;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinTable(name = "user_skype_call",
			joinColumns = {@JoinColumn(name = "skype_call_id", foreignKey = @ForeignKey(name = "FK_SKYPE_CALL_USER"))},
			inverseJoinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_SKYPE_CALL"))})
	private User fromAssignCall;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinTable(name = "client_skype_call",
			joinColumns = {@JoinColumn(name = "skype_call_id", foreignKey = @ForeignKey(name = "FK_SKYPE_CALL_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_SKYPE_CALL"))})
	private Client toAssignCall;

	@Column
	private Date dateOfSkypeCall;

	@Column
	private Date remindBeforeSkypeCall;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public User getFromAssignCall() {
		return fromAssignCall;
	}

	public void setFromAssignCall(User fromAssignCall) {
		this.fromAssignCall = fromAssignCall;
	}

	public Client getToAssignCall() {
		return toAssignCall;
	}

	public void setToAssignCall(Client toAssignCall) {
		this.toAssignCall = toAssignCall;
	}

	public Date getDateOfSkypeCall() {
		return dateOfSkypeCall;
	}

	public void setDateOfSkypeCall(Date dateOfSkypeCall) {
		this.dateOfSkypeCall = dateOfSkypeCall;
	}

	public Date getRemindBeforeSkypeCall() {
		return remindBeforeSkypeCall;
	}

	public void setRemindBeforeSkypeCall(Date remindBeforeSkypeCall) {
		this.remindBeforeSkypeCall = remindBeforeSkypeCall;
	}
}
