package com.ewp.crm.models;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "status_board",
//            joinColumns = {@JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_STATUS"))},
//            inverseJoinColumns = {@JoinColumn(name = "board_id", foreignKey = @ForeignKey(name = "FK_BOARD"))})
//    private Set<UserStatus> statuses;

    @Column(name = "name")
    private String name;

    public Board() {
    }

    public Board(String name) {
        this.name = name;
    }

//    public Board(Set<UserStatus> statuses, String name) {
//        this.statuses = statuses;
//        this.name = name;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
//
//    public Set<UserStatus> getStatuses() {
//        return statuses;
//    }
//
//    public void setStatuses(Set<UserStatus> statuses) {
//        this.statuses = statuses;
//    }
}
