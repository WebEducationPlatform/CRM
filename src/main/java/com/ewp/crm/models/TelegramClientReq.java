package com.ewp.crm.models;

import javax.persistence.*;

/**
 * Заявка от клиента через TelegramBot.
 */
@Entity
@Table(name = "telegram_client_req")
public class TelegramClientReq {

    @Id
    private Long id;
    // Основные данные
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "user_name")
    private String username;
    // Данные, полученные от клиента
    @Column(name = "input_name")
    private String inputName;
    private String email;
    private String phone;
    private String city;
    private String question;
    // Лучший способ отображения lazy связей OneToOne!
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Client client;

    public TelegramClientReq() {
    }

    public TelegramClientReq(Long chatId, Integer userId, String firstName, String lastName, String username) {
        this.chatId = chatId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TelegramClientReq clientReq = (TelegramClientReq) o;

        if (id != null ? !id.equals(clientReq.id) : clientReq.id != null) return false;
        if (!chatId.equals(clientReq.chatId)) return false;
        if (!userId.equals(clientReq.userId)) return false;
        if (!firstName.equals(clientReq.firstName)) return false;
        if (lastName != null ? !lastName.equals(clientReq.lastName) : clientReq.lastName != null) return false;
        if (username != null ? !username.equals(clientReq.username) : clientReq.username != null) return false;
        if (inputName != null ? !inputName.equals(clientReq.inputName) : clientReq.inputName != null) return false;
        if (email != null ? !email.equals(clientReq.email) : clientReq.email != null) return false;
        if (phone != null ? !phone.equals(clientReq.phone) : clientReq.phone != null) return false;
        if (city != null ? !city.equals(clientReq.city) : clientReq.city != null) return false;
        if (question != null ? !question.equals(clientReq.question) : clientReq.question != null) return false;
        return client != null ? client.equals(clientReq.client) : clientReq.client == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + chatId.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (inputName != null ? inputName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (question != null ? question.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TelegramClientReq{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", inputName='" + inputName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", question='" + question + '\'' +
                ", client=" + client +
                '}';
    }
}