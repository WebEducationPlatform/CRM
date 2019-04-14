package com.ewp.crm.models;



import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "assign_skype_call")
public class AssignSkypeCall {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "assign_skype_call_id")
	private Long id;

	@Column(name = "assign_skype_call_created_time")
	private ZonedDateTime createdTime;

	@Column(name = "skype_call_date")
	private ZonedDateTime skypeCallDate;

	@Column(name = "notification_before_of_skype_call")
	private ZonedDateTime notificationBeforeOfSkypeCall;

	@Column(name = "select_network_for_notifications")
	private String selectNetworkForNotifications;

	@Column(name = "the_notification_was_is_sent")
	private boolean theNotificationWasIsSent;

	@Column(name = "skype_call_date_completed")
	private boolean skypeCallDateCompleted;

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

	@ManyToOne
	@JoinTable(name = "assign_admin_skype_call",
			joinColumns = {@JoinColumn(name = "assign_skype_call_id", foreignKey = @ForeignKey(name = "FK_ASSIGN_SKYPE_CALL"))},
			inverseJoinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_ASSIGN_SKYPE_CALL_USER"))})
	private User whoCreatedTheSkypeCall;

	public AssignSkypeCall() {
	}

	public AssignSkypeCall(User whoCreatedTheSkypeCall,
						   Client toAssignSkypeCall,
						   ZonedDateTime createdTime,
						   ZonedDateTime skypeCallDate,
						   ZonedDateTime notificationBeforeOfSkypeCall) {
		this.whoCreatedTheSkypeCall = whoCreatedTheSkypeCall;
		this.fromAssignSkypeCall = whoCreatedTheSkypeCall;
		this.toAssignSkypeCall = toAssignSkypeCall;
		this.createdTime = createdTime;
		this.skypeCallDate = skypeCallDate;
		this.notificationBeforeOfSkypeCall = notificationBeforeOfSkypeCall;
		this.selectNetworkForNotifications = null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ZonedDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(ZonedDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public ZonedDateTime getSkypeCallDate() {
		return skypeCallDate;
	}

	public void setSkypeCallDate(ZonedDateTime skypeCallDate) {
		this.skypeCallDate = skypeCallDate;
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

	public User getWhoCreatedTheSkypeCall() {
		return whoCreatedTheSkypeCall;
	}

	public void setWhoCreatedTheSkypeCall(User whoCreatedTheSkypeCall) {
		this.whoCreatedTheSkypeCall = whoCreatedTheSkypeCall;
	}

	public boolean isTheNotificationWasIsSent() {
		return theNotificationWasIsSent;
	}

	public void setTheNotificationWasIsSent(boolean theNotificationWasIsSent) {
		this.theNotificationWasIsSent = theNotificationWasIsSent;
	}

	public boolean isSkypeCallDateCompleted() {
		return skypeCallDateCompleted;
	}

	public void setSkypeCallDateCompleted(boolean skypeCallDateCompleted) {
		this.skypeCallDateCompleted = skypeCallDateCompleted;
	}
}