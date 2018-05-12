package com.ewp.crm.models;

import com.ewp.crm.utils.patterns.ValidationPattern;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "client")
public class Client implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "client_id")
	private Long id;

	@NotNull
	@Column(name = "first_name", nullable = false)
	private String name;

	private String lastName;

	@Column(name = "phoneNumber", unique = true)
	private String phoneNumber;

	@Size(max = 50)
	@Email(regexp = ValidationPattern.EMAIL_PATTERN)
	@Column(name = "email", length = 50, unique = true)
	private String email;

	private byte age;

	@Enumerated(EnumType.STRING)
	private Sex sex;

	private String city;

	private String country;

	private String comment;

	@Column(name = "postponeDate")
	private Date postponeDate;

	@Column(name = "client_state")
	@Enumerated(EnumType.STRING)
	private State state;

	@Column(name = "date")
	private Date dateOfRegistration;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "status_id")
	@JoinTable(name = "status_users",
			joinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))},
			inverseJoinColumns = {@JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_STATUS"))})
	private Status status;

	@ManyToOne
	@JoinColumn(name = "owner_user_id")
	private User ownerUser;

	@JsonIgnore
	@OrderBy("date DESC")
	@OneToMany
	@JoinTable(name = "client_comment",
			joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_COMMENT_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "FK_COMMENT"))})
	private List<Comment> comments;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "client_notification",
			joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_NOTIFICATION_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "notification_id", foreignKey = @ForeignKey(name = "FK_NOTIFICATION"))})
	private List<Notification> notifications;

	@JsonManagedReference
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "history_client",
			joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "history_id", foreignKey = @ForeignKey(name = "FK_HISTORY"))})
	@OrderBy("id DESC")
	private List<ClientHistory> history = new ArrayList<>();

	@Column
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "client_job",
			joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "job_id", foreignKey = @ForeignKey(name = "FK_JOB"))})
	private List<Job> jobs;

	@Column
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "client_social_network",
			joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "social_network_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK"))})
	private List<SocialNetwork> socialNetworks;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "client_sms_info",
			joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "sms_info_id", foreignKey = @ForeignKey(name = "FK_SMS_INFO"))})
	private List<SMSInfo> smsInfo = new ArrayList<>();

	@Lob
	@Column(name = "client_description_comment")
	private String clientDescriptionComment;

	public Client() {
	}

	public Client(String name, String lastName) {
		this.name = name;
		this.lastName = lastName;
	}

	public Client(String name, String lastName, String phoneNumber, String email, byte age, Sex sex, Status status) {
		this.name = name;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.age = age;
		this.sex = sex;
		this.status = status;
	}

	public Client(String name, String lastName, String phoneNumber, String email, byte age, Sex sex) {
		this.name = name;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.age = age;
		this.sex = sex;
	}

	public Client(String name, String lastName, String phoneNumber, String email, byte age, Sex sex, String city, String country, State state, Date dateOfRegistration) {
		this.name = name;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.age = age;
		this.sex = sex;
		this.city = city;
		this.country = country;
		this.state = state;
		this.dateOfRegistration = dateOfRegistration;
	}


	public List<ClientHistory> getHistory() {
		return history;
	}

	public void setHistory(List<ClientHistory> history) {
		this.history = history;
	}

	public void addHistory(ClientHistory history) {
		this.history.add(history);
	}

	public void addSMSInfo(SMSInfo smsInfo) {
		this.smsInfo.add(smsInfo);
	}

	public Long getId() {
		return id;
	}

	public String getClientDescriptionComment() {
		return clientDescriptionComment;
	}

	public void setClientDescriptionComment(String clientDescriptionComment) {
		this.clientDescriptionComment = clientDescriptionComment;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getPostponeDate() {
		return postponeDate;
	}

	public void setPostponeDate(Date postponeDate) {
		this.postponeDate = postponeDate;
	}

	public boolean isActive() {
		return getPostponeDate()==null;
	}

	public byte getAge() {
		return age;
	}

	public void setAge(byte age) {
		this.age = age;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public User getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(User ownerUser) {
		this.ownerUser = ownerUser;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Date getDateOfRegistration() {
		return dateOfRegistration;
	}

	public void setDateOfRegistration(Date dateOfRegistration) {
		this.dateOfRegistration = dateOfRegistration;
	}

	public List<SocialNetwork> getSocialNetworks() {
		return socialNetworks;
	}

	public void setSocialNetworks(List<SocialNetwork> socialNetworks) {
		this.socialNetworks = socialNetworks;
	}

	//TODO На перемещение в контроллер
	@JsonIgnore
	public List<Notification> getSmsNotifications(){
		return this.notifications.stream().filter(n-> n.getType().equals(Notification.Type.SMS)).collect(Collectors.toList());
	}

	//TODO На перемещение в контроллер
	@JsonIgnore
	public List<Notification> getCommentNotifications(){
		return this.notifications.stream().filter(n-> n.getType().equals(Notification.Type.COMMENT)).collect(Collectors.toList());
	}

	//TODO На перемещение в контроллер
	@JsonIgnore
	public boolean ifPrincipalHaveNotifications(User user){
		return this.notifications.stream().anyMatch(n -> n.getUserToNotify().equals(user));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Client)) return false;

		Client client = (Client) o;

		if (id != null ? !id.equals(client.id) : client.id != null) return false;
		if (phoneNumber != null ? !phoneNumber.equals(client.phoneNumber) : client.phoneNumber != null) return false;
		return email != null ? email.equals(client.email) : client.email == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
		result = 31 * result + (email != null ? email.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Client: id: " + id + "; email: " + email + "; number: " + phoneNumber;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public List<SMSInfo> getSmsInfo() {
		return smsInfo;
	}

	public void setSmsInfo(List<SMSInfo> smsInfo) {
		this.smsInfo = smsInfo;
	}

	public enum Sex {
		MALE, FEMALE
	}

	public enum State {
		REFUSED, FINISHED, LEARNING, NEW
	}

}
