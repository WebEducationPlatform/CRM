package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "client_other_information")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClientOtherInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Название поля
     */
    @Column(name = "name_field")
    private String nameField;

    /**
     * Тип поля, на данный момент просто текст или checkbox
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

    public ClientOtherInformation() {
    }

    public ClientOtherInformation(String nameField, String typeField) {
        this.nameField = nameField;
        this.typeField = typeField;
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
}
