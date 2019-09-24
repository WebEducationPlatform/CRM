package com.ewp.crm.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor")
public class Mentor extends User {
    /**
     *Ддя ментора показывать ли только свои пользователей
     */
    @Column(name = "mentor_show_only_my_clients")
    private boolean showOnlyMyClients;

    //Связь с направлениями
    @ManyToMany(mappedBy = "clients")
    private List<Course> courses = new ArrayList<>();

    public Mentor() {
    }

    public Mentor(boolean showOnlyMyClients) {
        super();
        this.showOnlyMyClients = showOnlyMyClients;
    }

    public boolean isShowOnlyMyClients() {
        return showOnlyMyClients;
    }

    public void setShowOnlyMyClients(boolean showOnlyMyClients) {
        this.showOnlyMyClients = showOnlyMyClients;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}