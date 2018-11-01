package com.ewp.crm.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name = "assign_skype_call")
public class AssignSkypeCall {

	@Id
	@GeneratedValue
	@Column(name = "assign_skype_call_id")
	private Long id;

	@Column(name = "assign_skype_call_login")
	private String skypeClientlogin;

	@Column(name = "assign_skype_call_created_time")
	private ZonedDateTime createdTime;

	@Column(name = "date_Skype_Call")
	private ZonedDateTime dateSkypeCall;

	@Column(name = "notification_before_of_skype_call")
	private ZonedDateTime notificationBeforeOfSkypeCall;

	@Column(name = "select_network_for_notifications")
	private String selectNetworkForNotifications;

	@Column(name = "the_notification_is_sent")
	private boolean TheNotificationIsSent;

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

	public AssignSkypeCall() {
	}

	public AssignSkypeCall(String skypeClientlogin,
						   User fromAssignSkypeCall,
						   Client toAssignSkypeCall,
						   ZonedDateTime createdTime,
						   ZonedDateTime dateSkypeCall,
						   ZonedDateTime notificationBeforeOfSkypeCall,
						   String selectNetworkForNotifications) {
		this.skypeClientlogin = skypeClientlogin;
		this.fromAssignSkypeCall = fromAssignSkypeCall;
		this.toAssignSkypeCall = toAssignSkypeCall;
		this.createdTime = createdTime;
		this.dateSkypeCall = dateSkypeCall;
		this.notificationBeforeOfSkypeCall = notificationBeforeOfSkypeCall;
		this.selectNetworkForNotifications = selectNetworkForNotifications;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSkypeClientlogin() {
		return skypeClientlogin;
	}

	public void setSkypeClientlogin(String skypeClientlogin) {
		this.skypeClientlogin = skypeClientlogin;
	}

	public ZonedDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(ZonedDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public ZonedDateTime getDateSkypeCall() {
		return dateSkypeCall;
	}

	public void setDateSkypeCall(ZonedDateTime dateSkypeCall) {
		this.dateSkypeCall = dateSkypeCall;
	}

	public ZonedDateTime getNotificationBeforeOfSkypeCall() {
		return notificationBeforeOfSkypeCall;
	}

	public void setNotificationBeforeOfSkypeCall(ZonedDateTime notificationBeforeOfSkypeCall) {
		this.notificationBeforeOfSkypeCall = notificationBeforeOfSkypeCall;
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

	public boolean isTheNotificationIsSent() {
		return TheNotificationIsSent;
	}

	public void setTheNotificationIsSent(boolean theNotificationIsSent) {
		TheNotificationIsSent = theNotificationIsSent;
	}
}