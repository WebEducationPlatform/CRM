package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table(name = "last_contract_id")
public class LastContractId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "last_contract_id")
    private Long lastId;

    public LastContractId() {
    }

    public Long getId() {
        return id;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }
}
