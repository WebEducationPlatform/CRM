package com.ewp.crm.models;

import com.ewp.crm.models.whatsapp.WhatsappMessage;
import com.ewp.crm.utils.patterns.ValidationPattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import org.hibernate.annotations.Formula;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
@Table(name = "client")
public class Client implements Serializable, Diffable<Client> {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "client_id")
	private Long id;

	@NotNull
	@Column(name = "first_name", nullable = false)
	private String name;

    @Column(name = "middle_name")
    private String middleName;

	@Column(name = "last_name")
	private String lastName;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Size(max = 50)
    @Email(regexp = ValidationPattern.EMAIL_PATTERN)
    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "skype")
    private String skype = "";

    @Column(name = "birthDate")
    private LocalDate birthDate;

    @Formula("(if(birth_date is null,0,YEAR(CURDATE()) - YEAR(birth_date)))")
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex")
    private Sex sex;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "comment")
    private String comment;

    @Column(name = "university")
    private String university;

    @Column(name = "request_from")
    private String requestFrom;

    @Column(name = "postponeDate")
    private ZonedDateTime postponeDate;

    @Column(name = "can_call")
    private boolean canCall;

    @Column(name = "client_state")
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "date")
    private ZonedDateTime dateOfRegistration;

    @Column(name = "hide_card")
    private boolean isHideCard;

    @Column(name = "isRepeated")
    private boolean isRepeated;

    @Column(name = "postpone_comment")
    private String postponeComment;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "client_whatsapp_message",
            joinColumns = {@JoinColumn(name = "client_id",foreignKey = @ForeignKey(name = "FK_WHATSAPP_MESSAGE_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "whatsapp_message_number",foreignKey = @ForeignKey(name = "FK_WHATSAPP_MESSAGE"))})
    private List<WhatsappMessage> whatsappMessages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "status_id")
    @JoinTable(name = "status_clients",
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
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "client_notification",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_NOTIFICATION_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "notification_id", foreignKey = @ForeignKey(name = "FK_NOTIFICATION"))})
    private List<Notification> notifications = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "history_client",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "history_id", foreignKey = @ForeignKey(name = "FK_HISTORY"))})
    @OrderBy("id DESC")
    private List<ClientHistory> history = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "feedback_client",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "feedback_id", foreignKey = @ForeignKey(name = "FK_FEEDBACK"))})
    @OrderBy("id DESC")
    private List<ClientFeedback> feedback = new ArrayList<>();

    @Column
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "client_job",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "job_id", foreignKey = @ForeignKey(name = "FK_JOB"))})
    private List<Job> jobs = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "client_social_network",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "social_network_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK"))})
    private List<SocialProfile> socialProfiles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "client_sms_info",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "sms_info_id", foreignKey = @ForeignKey(name = "FK_SMS_INFO"))})
    private List<SMSInfo> smsInfo = new ArrayList<>();

    @Lob
    @Column(name = "client_description_comment", length = 1500)
    private String clientDescriptionComment;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
    private List<CallRecord> callRecords = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "client")
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    @JoinColumn(name = "slack_profile_id")
    private SlackProfile slackProfile;

    @Column(name = "live_skype_call")
    private boolean liveSkypeCall;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Passport passport;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private ContractLinkData contractLinkData;

    public Client() {
        this.state = State.NEW;
        this.dateOfRegistration = ZonedDateTime.now();
    }

    public Client(String name, String lastName) {
        this();
        this.name = name;
        this.lastName = lastName;
    }

    public Client(@NotNull String name, String phoneNumber, ZonedDateTime dateOfRegistration) {
        this();
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.dateOfRegistration = dateOfRegistration;
    }

    public Client(String name, String lastName, String phoneNumber, String email, LocalDate birthDate, Sex sex, Status status) {
        this();
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDate = birthDate;
        this.sex = sex;
        this.status = status;
    }

    public Client(String name, String lastName, String phoneNumber, String email, LocalDate birthDate, Sex sex) {
        this();
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDate = birthDate;
        this.sex = sex;
    }

    public Client(String name, String lastName, String phoneNumber, String email, LocalDate birthDate, Sex sex, String city, String country, State state, ZonedDateTime dateOfRegistration) {
        this();
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDate = birthDate;
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

    public boolean isLiveSkypeCall() {
        return liveSkypeCall;
    }

    public void setLiveSkypeCall(boolean liveSkypeCall) {
        this.liveSkypeCall = liveSkypeCall;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
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

    public ZonedDateTime getPostponeDate() {
        return postponeDate;
    }

    public void setPostponeDate(ZonedDateTime postponeDate) {
        this.postponeDate = postponeDate;
    }

    public boolean isActive() {
        return getPostponeDate() == null;
    }

    public int getAge() {
        return age;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getRequestFrom() {
        return requestFrom;
    }

    public void setRequestFrom(String requestFrom) {
        this.requestFrom = requestFrom;
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

    public List<WhatsappMessage> getWhatsappMessages() {
        return whatsappMessages;
    }

    public void setWhatsappMessages(List<WhatsappMessage> whatsappMessages) {
        this.whatsappMessages = whatsappMessages;
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

    public ZonedDateTime getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(ZonedDateTime dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public List<SocialProfile> getSocialProfiles() {
        return socialProfiles;
    }

    public void setSocialProfiles(List<SocialProfile> socialProfiles) {
        this.socialProfiles = socialProfiles;
    }

    public void addSocialProfile(SocialProfile socialProfile) {
        this.socialProfiles.add(socialProfile);
    }

    public void deleteSocialProfile(SocialProfile socialProfile) {
        this.socialProfiles.remove(socialProfile);
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public SlackProfile getSlackProfile() {
        return slackProfile;
    }

    public void setSlackProfile(SlackProfile slackProfile) {
        this.slackProfile = slackProfile;
    }

    public boolean isRepeated() {
        return isRepeated;
    }

    public void setRepeated(boolean repeated) {
        isRepeated = repeated;
    }

    public boolean isHideCard() {
        return isHideCard;
    }

    public void setHideCard(boolean hideCard) {
        isHideCard = hideCard;
    }

    public String getPostponeComment() {
        return postponeComment;
    }

    public void setPostponeComment(String postponeComment) {
        this.postponeComment = postponeComment;
    }

    public Passport getPassport() {
        return passport;
    }

    public void setPassport(Passport passport) {
        this.passport = passport;
    }

    public ContractLinkData getContractLinkData() {
        return contractLinkData;
    }

    public void setContractLinkData(ContractLinkData contractLinkData) {
        this.contractLinkData = contractLinkData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return  Objects.equals(id, client.id) &&
                Objects.equals(name, client.name) &&
                Objects.equals(lastName, client.lastName) &&
                Objects.equals(phoneNumber, client.phoneNumber) &&
                Objects.equals(email, client.email) &&
                sex == client.sex &&
                Objects.equals(city, client.city) &&
                Objects.equals(country, client.country) &&
                state == client.state &&
                Objects.equals(socialProfiles, client.socialProfiles) &&
                Objects.equals(jobs, client.jobs) &&
                Objects.equals(skype, client.skype) &&
                Objects.equals(postponeDate, client.postponeDate)&&
                Objects.equals(birthDate, client.birthDate) &&
                Objects.equals(university, client.university) &&
                Objects.equals(requestFrom, client.requestFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName, phoneNumber, email, skype, sex, city, country,
                state, jobs, socialProfiles, postponeDate, birthDate, university,requestFrom);
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

    public boolean isCanCall() {
        return canCall;
    }

    public void setCanCall(boolean canCall) {
        this.canCall = canCall;
    }

    public List<CallRecord> getCallRecords() {
        return callRecords;
    }

    public void setCallRecords(List<CallRecord> callRecords) {
        this.callRecords = callRecords;
    }

    public void addCallRecord(CallRecord callRecord) {
        this.callRecords.add(callRecord);
    }

    public List<ClientFeedback> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<ClientFeedback> feedback) {
        this.feedback = feedback;
    }

    public void addFeedback(ClientFeedback feedback) {
        this.feedback.add(feedback);
    }

    @Override
    public DiffResult diff(Client client) {
        return new DiffBuilder(this, client, ToStringStyle.JSON_STYLE)
                .append("Имя", this.name, client.name)
                .append("Фамилия", this.lastName, client.lastName)
                .append("Номер телефона", this.phoneNumber, client.phoneNumber)
                .append("E-mail", this.email, client.email)
                .append("Skype", this.skype, client.skype)
                .append("Дата рождения", this.birthDate, client.birthDate)
                .append("Пол", this.sex, client.sex)
                .append("Страна", this.country, client.country)
                .append("Город", this.city, client.city)
                .append("Работа", this.jobs.toString(), client.jobs.toString())
                .append("Социальные сети", this.socialProfiles.toString(), client.socialProfiles.toString())
                .append("Состояние", this.state, client.state)
                .build();
    }

    public DiffResult diffOnStudentEdit(Client client) {
        return new DiffBuilder(this, client, ToStringStyle.JSON_STYLE)
                .append("Имя", this.name, client.name)
                .append("Фамилия", this.lastName, client.lastName)
                .append("E-mail", this.email, client.email)
                .build();
    }

    public enum Sex {
        MALE, FEMALE
    }

    public enum State {
        NEW,
        LEARNING,
        FINISHED,
        REFUSED
    }

}
