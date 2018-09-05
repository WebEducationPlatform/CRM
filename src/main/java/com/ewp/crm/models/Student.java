package com.ewp.crm.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table (name = "student")
@Entity
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    @Column (name = "client_id")
    @OneToOne
    private Client client;

    @Column (name = "end_trial")
    private LocalDateTime trialEndDate;

    @Column (name = "next_pay")
    private LocalDateTime nextPaymentDate;

    @Column (name = "price")
    private Long price;

    @Column (name = "amount")
    private Long paymentAmount;

    @Column (name = "later")
    private Long payLater;

    @Column (name = "status_id")
    @OneToOne
    private StudentStatus status;

    @Column (name = "notes")
    private String notes;
}
