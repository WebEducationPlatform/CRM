package com.ewp.crm.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_task")
public class UserTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="task")
    private String task;

    @Column(name="date")
    private LocalDate date;

    @Column(name="expiry_date")
    private LocalDate expiry_date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "author_user_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "manager_user_id", nullable = false)
    private User manager;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "executor_user_id", nullable = false)
    private User executor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "client_id", nullable = false)
    private Client client;

    public UserTask() {
    }

    public UserTask(String task, LocalDate date,  LocalDate expiry_date, User author,  User manager, User executor, Client client) {
        this.task = task;
        this.date = date;
        this.expiry_date = expiry_date;
        this.author = author;
        this.manager = manager;
        this.executor = executor;
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDate getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(LocalDate expiry_date) {
        this.expiry_date = expiry_date;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getExecutor() {
        return executor;
    }

    public void setExecutor(User executor) {
        this.executor = executor;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }
}
