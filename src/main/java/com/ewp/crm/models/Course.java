package com.ewp.crm.models;

import javax.persistence.*;
import java.util.List;
/*
Направления
 */
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(name = "course_name", nullable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(name = "courses_clients",
            joinColumns = {@JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "FK_COURSE"))},
            inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))})
    private List<Client> clients;

    @ManyToMany
    @JoinTable(name = "courses_users",
            joinColumns = {@JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "FK_COURSE"))},
            inverseJoinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))})
    private List<User> users;

//Конструкторы
    public Course() {
    }

    public Course(String name) {
        this.name = name;
    }

    public Course(String name, List<Client> clients) {
        this.name = name;
        this.clients = clients;
    }

    public Course(String name, List<Client> clients, List<User> users) {
        this.name = name;
        this.clients = clients;
        this.users = users;
    }
//Геттеры, сеттеры
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
