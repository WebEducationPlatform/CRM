package com.ewp.crm.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mentor")
public class Mentor extends User {
    /**
     *Ддя ментора показывать ли только свои пользователей
     */
    @Column(name = "mentor_show_only_my_clients")
    private boolean showOnlyMyClients;

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
}