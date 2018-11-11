package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "vk_request_form")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VkRequestForm {
    @Id
    @Column(name = "number_vk_field")
    private Long numberVkField;

    @Column(name = "name_vk_field")
    private String nameVkField;

    @Column(name = "type_vk_field")
    private String typeVkField;

    public VkRequestForm() {

    }

    public Long getNumberVkField() {
        return numberVkField;
    }

    public void setNumberVkField(Long numberVkField) {
        this.numberVkField = numberVkField;
    }

    public VkRequestForm(Long numberVkField, String nameVkField, String typeVkField) {
        this.numberVkField = numberVkField;
        this.nameVkField   = nameVkField;
        this.typeVkField   = typeVkField;
    }

    public String getNameVkField() {
        return nameVkField;
    }

    public void setNameVkField(String nameVkField) {
        this.nameVkField = nameVkField;
    }

    public String getTypeVkField() {
        return typeVkField;
    }

    public void setTypeVkField(String typeVkField) {
        this.typeVkField = typeVkField;
    }
}