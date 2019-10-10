package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @JsonIgnore
    @JoinTable(name = "courses_clients",
            joinColumns = {@JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "FK_COURSE"))},
            inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))})
    private Set<Client> clients = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "courses_mentors",
            joinColumns = {@JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "FK_COURSE"))},
            inverseJoinColumns = {@JoinColumn(name = "mentor_id", foreignKey = @ForeignKey(name = "FK_MENTOR"))})
    private List<Mentor> mentors  = new ArrayList<>();

//Для оздания однонаправленной связи допускается удаление
    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private Set<StudentEducationStage> studentEducationStage;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private Set<CourseSet> courseSets;

//Конструкторы
    public Course() {
    }

    public Course(String name) {
        this.name = name;
    }

    public Course(String name, Set<Client> clients) {
        this.name = name;
        this.clients = clients;
    }

    public Course(String name, Set<Client> clients, List<Mentor> mentors) {
        this.name = name;
        this.clients = clients;
        this.mentors = mentors;
    }

    public Course(String name, Set<Client> clients, List<Mentor> mentors,
                  Set<StudentEducationStage> studentEducationStage) {
        this.name = name;
        this.clients = clients;
        this.mentors = mentors;
        this.studentEducationStage = studentEducationStage;
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

    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public void setClient(Client client) {
        clients.add(client);
    }

    public List<Mentor> getMentors() {
        return mentors;
    }

    public void setMentors(List<Mentor> mentors) {
        this.mentors = mentors;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<StudentEducationStage> getStudentEducationStage() {
        return studentEducationStage;
    }

    public void setStudentEducationStage(Set<StudentEducationStage> studentEducationStage) {
        this.studentEducationStage = studentEducationStage;
    }

    public Set<CourseSet> getCourseSets() {
        return courseSets;
    }

    public void setCourseSets(Set<CourseSet> courseSets) {
        this.courseSets = courseSets;
    }
}
