package com.ewp.crm.models;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity(name = "mentor")
@Table(name = "mentor")
@DiscriminatorValue(value = "MENTOR")
public class Mentor extends User {
    /**
     * Ддя ментора показывать ли только свои пользователей
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

    public Mentor(User user) {
        super(user.getFirstName(), user.getLastName(), user.getBirthDate(), user.getPhoneNumber(), user.getEmail(), user.getPassword(),
                user.getVk(), user.getSex(), user.getCity(), user.getCountry(), user.getRole(), user.isIpTelephony(), user.isVerified());
    super.setId(user.getId());
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