package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table(name = "answers")
public class AutoAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "subject", unique = true)
    private String subject;

    @OneToOne
    @JoinColumn (name = "messagetemplate_id")
    private MessageTemplate messageTemplate;

    @OneToOne
    @JoinColumn (name = "status_id")
    private Status status;
}
