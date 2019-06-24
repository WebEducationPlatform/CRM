package com.ewp.crm.models.whatsapp;


import com.ewp.crm.models.Client;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.TimeZone;

@Entity
@Table(name = "whatsapp_message")
public class WhatsappMessage {


    @Column(name = "whatsapp_id",unique = true,nullable = true)
    private String id;

    @Column(name = "body")
    private String body;

    @Column(name = "fromMe")
    private boolean fromMe;

    @Column(name = "author")
    private String author;

    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "chatId")
    private String chatId;
    @Id
    @Column(name = "whatsapp_message_number")
    //need to be made id in DB
    private long messageNumber;

    @Column(name = "type")
    private String type;

    @Column(name = "senderName")
    private String senderName;

    @Column(name = "caption")
    private String caption;

    @JsonIgnore
    @ManyToOne(targetEntity = Client.class, fetch = FetchType.LAZY)
    @JoinTable(name = "client_whatsapp_message",
            joinColumns = {@JoinColumn(name = "whatsapp_message_number", foreignKey = @ForeignKey(name = "FK_WHATSAPP_MESSAGE"))},
            inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_WHATSAPP_MESSAGE_CLIENT"))})
    private Client client;

    @Column(name = "seen")
    private boolean seen = false;

    public WhatsappMessage() {

    }

    public WhatsappMessage(String body, boolean fromMe, ZonedDateTime time, String chatId, long messageNumber, String senderName, Client client) {
        this.body = body;
        this.fromMe = fromMe;
        this.time = time;
        this.chatId = chatId;
        this.messageNumber = messageNumber;
        this.senderName = senderName;
        this.client = client;
    }

    public WhatsappMessage(String id, String body, boolean fromMe, ZonedDateTime time, String chatId, long messageNumber, String senderName, Client client) {
        this.id = id;
        this.body = body;
        this.fromMe = fromMe;
        this.time = time;
        this.chatId = chatId;
        this.messageNumber = messageNumber;
        this.senderName = senderName;
        this.client = client;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    //из API приходит время в формате unix timestamp сдесь оно конвертируеться в ZonedDataTime
    public void setTime(long time) {
        this.time = Instant.ofEpochSecond(time).atZone(TimeZone.getDefault().toZoneId());
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public long getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(long messageNumber) {
        this.messageNumber = messageNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean isSeen) {
        this.seen = isSeen;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WhatsappMessage)) return false;

        WhatsappMessage that = (WhatsappMessage) o;

        if (isFromMe() != that.isFromMe()) return false;
        if (getMessageNumber() != that.getMessageNumber()) return false;
        if (isSeen() != that.isSeen()) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getBody() != null ? !getBody().equals(that.getBody()) : that.getBody() != null) return false;
        if (getAuthor() != null ? !getAuthor().equals(that.getAuthor()) : that.getAuthor() != null) return false;
        if (getTime() != null ? !getTime().equals(that.getTime()) : that.getTime() != null) return false;
        if (getChatId() != null ? !getChatId().equals(that.getChatId()) : that.getChatId() != null) return false;
        if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) return false;
        if (getSenderName() != null ? !getSenderName().equals(that.getSenderName()) : that.getSenderName() != null)
            return false;
        if (getCaption() != null ? !getCaption().equals(that.getCaption()) : that.getCaption() != null) return false;
        return getClient() != null ? getClient().equals(that.getClient()) : that.getClient() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getBody() != null ? getBody().hashCode() : 0);
        result = 31 * result + (isFromMe() ? 1 : 0);
        result = 31 * result + (getAuthor() != null ? getAuthor().hashCode() : 0);
        result = 31 * result + (getTime() != null ? getTime().hashCode() : 0);
        result = 31 * result + (getChatId() != null ? getChatId().hashCode() : 0);
        result = 31 * result + (int) (getMessageNumber() ^ (getMessageNumber() >>> 32));
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getSenderName() != null ? getSenderName().hashCode() : 0);
        result = 31 * result + (getCaption() != null ? getCaption().hashCode() : 0);
        result = 31 * result + (getClient() != null ? getClient().hashCode() : 0);
        result = 31 * result + (isSeen() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WhatsappMessage{" +
                ", id='" + id + '\'' +
                ", body='" + body + '\'' +
                ", fromMe=" + fromMe +
                ", author='" + author + '\'' +
                ", time=" + time +
                ", chatId='" + chatId + '\'' +
                ", messageNumber=" + messageNumber +
                ", type='" + type + '\'' +
                ", senderName='" + senderName + '\'' +
                ", caption='" + caption + '\'' +
                ", client=" + client +
                ", seen=" + seen +
                '}';
    }
}
