package com.ewp.crm.models;

import com.ewp.crm.models.whatsapp.WhatsappMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
@Entity
@Table(name = "client")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    /**
     * We reduce number of requests with FetchType.EAGER.
     * ElementCollection uses cascadeType.ALL and orphanRemoval = true by default.
     * We use BatchSize to control our queries and not request too many entities.
     * OrderColumn used to maintain the persistent order of a list.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="client_phones", joinColumns = @JoinColumn(name="client_id"))
    @Column(name="client_phone", unique = true)
    @OrderColumn(name = "numberInList")
    @BatchSize(size = 30)
    private List<String> clientPhones = new ArrayList<>();

    /**
     * We reduce number of requests with FetchType.EAGER.
     * ElementCollection uses cascadeType.ALL and orphanRemoval = true by default.
     * We use BatchSize to control our queries and not request too many entities.
     * OrderColumn used to maintain the persistent order of a list.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="client_emails", joinColumns = @JoinColumn(name="client_id"))
    @Column(name="client_email", unique = true)
    @OrderColumn(name = "numberInList")
    @BatchSize(size = 30)
    private List<String> clientEmails = new ArrayList<>();

    @Column(name = "skype")
    private String skype = "";

    @Column(name = "birth_date")
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

    /**
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     * OneToMany uses FetchType.LAZY by default.
     * We use BatchSize to control our queries and not request too many entities.
     */
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    @JoinTable(name = "client_whatsapp_message",
            joinColumns = {@JoinColumn(name = "client_id",foreignKey = @ForeignKey(name = "FK_WHATSAPP_MESSAGE_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "whatsapp_message_number",foreignKey = @ForeignKey(name = "FK_WHATSAPP_MESSAGE"))})
    private List<WhatsappMessage> whatsappMessages = new ArrayList<>();

    /**
     * We use FetchType.LAZY for lazy initialization.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    @JoinTable(name = "status_clients",
            joinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))},
            inverseJoinColumns = {@JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_STATUS"))})
    private Status status;

    /**
     * ManyToOne uses fetchType.EAGER by default.
     */
    @ManyToOne
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;

    /**
     * ManyToOne uses fetchType.EAGER by default.
     */
    @ManyToOne
    @JoinColumn(name = "owner_mentor_id")
    private User ownerMentor;

    /**
     * OrderBy determines the ordering of the elements.
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     * OneToMany uses FetchType.LAZY by default.
     * We use BatchSize to control our queries and not request too many entities.
     */
    @JsonIgnore
    @OrderBy("date DESC")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 25)
    @JoinTable(name = "client_comment",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_COMMENT_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "FK_COMMENT"))})
    private List<Comment> comments = new ArrayList<>();

    /**
     * OrderBy determines the ordering of the elements.
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     * OneToMany uses FetchType.LAZY by default.
     * We use BatchSize to control our queries and not request too many entities.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "history_client",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "history_id", foreignKey = @ForeignKey(name = "FK_HISTORY"))})
    @OrderBy("id DESC")
    @BatchSize(size = 25)
    private List<ClientHistory> history = new ArrayList<>();

    /**
     * OrderBy determines the ordering of the elements.
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     * OneToMany uses FetchType.LAZY by default.
     * We use BatchSize to control our queries and not request too many entities.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "feedback_client",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "feedback_id", foreignKey = @ForeignKey(name = "FK_FEEDBACK"))})
    @OrderBy("id DESC")
    @BatchSize(size = 25)
    private List<ClientFeedback> feedback = new ArrayList<>();

    /**
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     * OneToMany uses FetchType.LAZY by default.
     * We use BatchSize to control our queries and not request too many entities.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "client_job",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "job_id", foreignKey = @ForeignKey(name = "FK_JOB"))})
    @BatchSize(size = 25)
    private List<Job> jobs = new ArrayList<>();

    /**
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     * We use FetchType.EAGER because this field is used in Transactional method and have to initialize before.
     * We use BatchSize to control our queries and not request too many entities.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 30)
    @JoinTable(name = "client_social_network",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "social_network_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK"))})
    private List<SocialProfile> socialProfiles = new ArrayList<>();

    /**
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     * OneToMany uses FetchType.LAZY by default.
     * We use BatchSize to control our queries and not request too many entities.
     */
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    @JoinTable(name = "client_sms_info",
            joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
            inverseJoinColumns = {@JoinColumn(name = "sms_info_id", foreignKey = @ForeignKey(name = "FK_SMS_INFO"))})
    private List<SMSInfo> smsInfo = new ArrayList<>();

    @Lob
    @Column(name = "client_description_comment", length = 1500)
    private String clientDescriptionComment;

    /**
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     * OneToMany uses FetchType.LAZY by default.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CallRecord> callRecords = new ArrayList<>();

    /**
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     */
    @JsonIgnore
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "student_id")
    private Student student;

    /**
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     */
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "slack_invite_link_id")
    private SlackInviteLink slackInviteLink;

    public User getOwnerMentor() {
        return ownerMentor;
    }

    public void setOwnerMentor(User ownerMentor) {
        this.ownerMentor = ownerMentor;
    }

    @Column(name = "live_skype_call")
    private boolean liveSkypeCall;

    /**
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     */
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Passport passport;

    /**
     * We use CascadeType.ALL to manage entity through Client's entity.
     * OrphanRemoval needs for a disconnected instance is automatically removed.
     */
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private ContractLinkData contractLinkData;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private OtherInformationLinkData otherInformationLinkData;

    @Column(name = "minutes_to_first_call_with_hr")
    private Integer minutesToFirstCallWithHr;

    public Client() {}

    private Client(Builder builder) {
        name = builder.name;
        middleName = builder.middleName;
        lastName = builder.lastName;
        if (builder.phone != null) clientPhones.add(builder.phone);
        if (builder.email != null) clientEmails.add(builder.email);
        skype = builder.skype;
        birthDate = builder.birthDate;
        sex = builder.sex;
        city = builder.city;
        country = builder.country;
        state = builder.state;
        dateOfRegistration = builder.dateOfRegistration;
    }

    public Integer getMinutesToFirstCallWithHr() {
        return minutesToFirstCallWithHr;
    }

    public void setMinutesToFirstCallWithHr(Integer minutesToFirstCallWithHr) {
        this.minutesToFirstCallWithHr = minutesToFirstCallWithHr;
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

    public Optional<String> getPhoneNumber() {
        return clientPhones.isEmpty() ? Optional.empty() : Optional.ofNullable(clientPhones.get(0));
    }

    public void setPhoneNumber(String phoneNumber) {
        if (clientPhones.isEmpty()) {
            clientPhones.add(phoneNumber);
        } else {
            clientPhones.set(0, phoneNumber);
        }
    }

    public Optional<String> getEmail() {
        return clientEmails.isEmpty() ? Optional.empty() : Optional.ofNullable(clientEmails.get(0));
    }

    public void setEmail(String email) {
        if (clientEmails.isEmpty()) {
            clientEmails.add(email);
        } else {
            clientEmails.set(0, email);
        }
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

    public void setAge(int age) {
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

    public List<String> getClientPhones() {
        return clientPhones;
    }

    public void setClientPhones(List<String> clientPhones) {
        this.clientPhones = clientPhones;
    }

    public List<String> getClientEmails() {
        return clientEmails;
    }

    public void setClientEmails(List<String> clientEmails) {
        this.clientEmails = clientEmails;
    }

    public SlackInviteLink getSlackInviteLink() {
        return slackInviteLink;
    }

    public void setSlackInviteLink(SlackInviteLink slackInviteLink) {
        this.slackInviteLink = slackInviteLink;
    }

    public OtherInformationLinkData getOtherInformationLinkData() {
        return otherInformationLinkData;
    }

    public void setOtherInformationLinkData(OtherInformationLinkData otherInformationLinkData) {
        this.otherInformationLinkData = otherInformationLinkData;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return  Objects.equals(id, client.id) &&
                Objects.equals(name, client.name) &&
                Objects.equals(lastName, client.lastName) &&
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
                Objects.equals(requestFrom, client.requestFrom) &&
                Objects.equals(clientEmails, client.clientEmails) &&
                Objects.equals(clientPhones, client.clientPhones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName, skype, sex, city, country,
                state, jobs, socialProfiles, postponeDate, birthDate, university, requestFrom, clientEmails, clientPhones);
    }

    @Override
    public String toString() {
        return "Client: id: " + id + "; name: " + name + "; email: " +  getEmail().orElse("not found")  + "; phone number: "+ getPhoneNumber().orElse("not found")
                + "; city: " + Optional.ofNullable(city).orElse("not found") + "; country: " + Optional.ofNullable(country).orElse("not found")
                + "; request from: " + Optional.ofNullable(requestFrom).orElse("not found") + "; description comment: " + Optional.ofNullable(clientDescriptionComment).orElse("not found");
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

    public DiffResult diffByNameAndLastNameAndEmail(Client client) {
        return new DiffBuilder(this, client, ToStringStyle.JSON_STYLE)
                .append("Имя", this.name, client.name)
                .append("Фамилия", this.lastName, client.lastName)
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

    public static class Builder {

        private String name;
        private String middleName;
        private String lastName;
        private String phone;
        private String email;
        private String skype;
        private LocalDate birthDate;
        private Sex sex;
        private String city;
        private String country;

        private State state;
        private ZonedDateTime dateOfRegistration;

        public Builder(String name, String phone, String email) {
            this.state = State.NEW;
            this.dateOfRegistration = ZonedDateTime.now();
            this.name = name;
            this.phone = phone;
            this.email = email;
        }

        public Builder(String name) {
            this.state = State.NEW;
            this.dateOfRegistration = ZonedDateTime.now();
            this.name = name;
        }

        public Client build() {
            return new Client(this);
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder middleName(String middleName) {
            this.middleName = middleName;
            return this;
        }

        public Builder skype(String skype) {
            this.skype = skype;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder sex(Sex sex) {
            this.sex = sex;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }
    }
}
