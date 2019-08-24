package com.ewp.crm.models;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "mentor")
@Inheritance(strategy = InheritanceType.JOINED)
public class Mentor extends User {
    /**
     *Ддя ментора показывать ли только свои пользователей
     */
    @Column(name = "mentor_show_only_my_clients")
    @ColumnDefault("1")
    @NotNull
    private boolean showOnlyMyClients;
    public Mentor() {
    }

    @Column( name = "quantity_students")
    @ColumnDefault("3")
    @NotNull
    private int quantityStudents;

    public Mentor(boolean showOnlyMyClients) {
        super();
        this.showOnlyMyClients = showOnlyMyClients;
    }

    public int getQuantityStudents() {
        return quantityStudents;
    }

    public void setQuantityStudents(int quantityStudents) {
        this.quantityStudents = quantityStudents;
    }

    public boolean isShowOnlyMyClients() {
        return showOnlyMyClients;
    }

    public void setShowOnlyMyClients(boolean showOnlyMyClients) {
        this.showOnlyMyClients = showOnlyMyClients;
    }
}