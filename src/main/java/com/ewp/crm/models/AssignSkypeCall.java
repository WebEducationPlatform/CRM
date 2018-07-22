package com.ewp.crm.models;

import javax.persistence.*;
import java.util.Date;

@Entity
public class AssignSkypeCall {

	@Id
	@GeneratedValue
	@Column(name = "assign_skype_call_id")
	private Long id;

	@Column(name = "assign_skype_call_login")
	private String login;

	@Column(name = "assign_skype_call_created_time")
	private Date createdTime;

	@Column
	private Date remindBeforeOfSkypeCall;

	@Column
	private String selectNetworkForNotifications;

	@ManyToOne
	@JoinTable(name = "assign_user_skype_call",
			joinColumns = {@JoinColumn(name = "assign_skype_call_id", foreignKey = @ForeignKey(name = "FK_ASSIGN_SKYPE_CALL"))},
			inverseJoinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_ASSIGN_SKYPE_CALL_USER"))})
	private User fromAssignSkypeCall;

	@ManyToOne
	@JoinTable(name = "assign_client_skype_call",
			joinColumns = {@JoinColumn(name = "assign_skype_call_id", foreignKey = @ForeignKey(name = "FK_ASSIGN_SKYPE_CALL"))},
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_ASSIGN_SKYPE_CALL_CLIENT"))})
	private Client toAssignSkypeCall;


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

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getRemindBeforeOfSkypeCall() {
		return remindBeforeOfSkypeCall;
	}

	public void setRemindBeforeOfSkypeCall(Date remindBeforeOfSkypeCall) {
		this.remindBeforeOfSkypeCall = remindBeforeOfSkypeCall;
	}

	public String getSelectNetworkForNotifications() {
		return selectNetworkForNotifications;
	}

	public void setSelectNetworkForNotifications(String selectNetworkForNotifications) {
		this.selectNetworkForNotifications = selectNetworkForNotifications;
	}

	public User getFromAssignSkypeCall() {
		return fromAssignSkypeCall;
	}

	public void setFromAssignSkypeCall(User fromAssignSkypeCall) {
		this.fromAssignSkypeCall = fromAssignSkypeCall;
	}

	public Client getToAssignSkypeCall() {
		return toAssignSkypeCall;
	}

	public void setToAssignSkypeCall(Client toAssignSkypeCall) {
		this.toAssignSkypeCall = toAssignSkypeCall;
	}
}
