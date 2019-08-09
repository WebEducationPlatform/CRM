package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client_other_information")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClientOtherInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coi_id")
    private Long id;

    @Column(name = "name_field")
    private String nameField;

    /**
     * Тип поля: текст, checkbox или checkboxes
     */
    @Column(name = "type_field")
    private String typeField;

    /**
     * Значение true или false если значение имеет тип checkbox
     */
    @Column(name = "value_checkbox")
    private Boolean checkboxValue;

    /**
     * Значение если значение имеет тип text
     */
    @Column(name = "value_text")
    private String textValue;

    /**
     * Id клиента которому присвоена сущность
     */
    @Column(name = "client_id")
    private Long clientId;

    /**
     * Поле в карточке клиента (опционально)
     */
    @Column(name = "card_field")
    private String cardField;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "questions",
            joinColumns = { @JoinColumn(name = "coi_id") },
            inverseJoinColumns = { @JoinColumn(name = "oimc_id") })
    private List<OtherInformationMultipleCheckboxes> oimc = new ArrayList<>();

    public ClientOtherInformation() {
    }

    public ClientOtherInformation(String nameField, String typeField) {
        this.nameField = nameField;
        this.typeField = typeField;
    }

    public List<OtherInformationMultipleCheckboxes> getOimc() {
        return oimc;
    }

    public void setOimc(List<OtherInformationMultipleCheckboxes> oimc) {
        this.oimc = oimc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameField() {
        return nameField;
    }

    public void setNameField(String nameField) {
        this.nameField = nameField;
    }

    public String getTypeField() {
        return typeField;
    }

    public void setTypeField(String typeField) {
        this.typeField = typeField;
    }

    public Boolean getCheckboxValue() {
        return checkboxValue;
    }

    public void setCheckboxValue(Boolean checkboxValue) {
        this.checkboxValue = checkboxValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getCardField() {
        return cardField;
    }

    public void setCardField(String cardField) {
        this.cardField = cardField;
    }
}
