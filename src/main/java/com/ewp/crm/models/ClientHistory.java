package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "history")
public class ClientHistory {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "history_id")
	private Long id;

	@Column(name = "title", nullable = false)
	@Lob
    private String title;

	@Basic
	@Lob
    @Column(name = "link")
	private String link;

	@Column(name = "record_link")
	private String recordLink;
	
    @Column(name = "date")
    private ZonedDateTime date;

    @Column(name = "history_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @OneToOne(cascade = CascadeType.ALL)
    private Message message;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinTable(name = "history_client",
            joinColumns = {@JoinColumn(name = "history_id", foreignKey = @ForeignKey(name = "FK_HISTORY"))},
            inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))})
    private Client client;

    public ClientHistory() {
        this.date = ZonedDateTime.now();
    }

    public ClientHistory(Type type) {
        this();
        this.type = type;
    }

    public ClientHistory(String title, ZonedDateTime date, Type type) {
        this.title = title;
        this.date = date;
        this.type = type;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public Type getType() {
        return type;
    }

    public Message getMessage() {
        return message;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientHistory)) return false;
        ClientHistory that = (ClientHistory) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date);
    }

    public String getRecordLink() {
        return recordLink;
    }

    public void setRecordLink(String recordLink) {
        this.recordLink = recordLink;
    }

	public enum Type {
		SOCIAL_REQUEST("Клиент был добавлен из"),
		STATUS("переместил клиента в статус:"),
		DESCRIPTION("добавил комментарий к клиенту:"),
		POSTPONE("скрыл клиента до:"),
        REMOVE_POSTPONE("убрал скрытие"),
		NOTIFICATION("прочитал напоминание"),
		ASSIGN("прикрепил"),
		UNASSIGN("открепил"),
        ASSIGN_MENTOR("прикрепил ментора"),
        UNASSIGN_MENTOR("открепил ментора"),
		CALL("совершил(а) звонок"),
		CALL_WITHOUT_RECORD(", не дозвонился"),
		SEND_MESSAGE("отправил сообщение по"),
		ADD("добавил вручную"),
		UPDATE("обновил информацию"),
		SKYPE("назначил созвон по скайпу на"),
		SKYPE_UPDATE("изменил созвон по скайпу на"),
		SKYPE_DELETE("удалил созвон по скайпу на"),
		ADD_LOGIN("установил клиенту логин в skype - "),
        ADD_STUDENT("сделал клиента студентом"),
        UPDATE_STUDENT("обновил информацию студента"),
        DELETE_STUDENT("удалил студента"),
		SLACK_UPDATE("обновил данные из формы регистрации в Slack");

        private String info;

        Type(String info) {
            this.info = info;
        }

        public String getInfo() {
            return info;
        }
    }
}