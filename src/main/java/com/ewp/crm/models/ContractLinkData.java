package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table(name = "clients_contract_link")
public class ContractLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "contract_link")
    private String contractLink;

    public ContractLink() {
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getContractLink() {
        return contractLink;
    }

    public void setContractLink(String contractLink) {
        this.contractLink = contractLink;
    }
}
