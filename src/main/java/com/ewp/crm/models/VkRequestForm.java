package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Comparator;

@Entity
@Table(name = "vk_request_form")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VkRequestForm {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "number_vk_field")
    private Integer numberVkField;

    @Column(name = "name_vk_field")
    private String nameVkField;

    @Column(name = "type_vk_field")
    private String typeVkField;

    public VkRequestForm() {

    }

    public Integer getNumberVkField() {
        return numberVkField;
    }

    public void setNumberVkField(Integer numberVkField) {
        this.numberVkField = numberVkField;
    }

    public VkRequestForm(Long id, String nameVkField, String typeVkField) {
        this.id = id;
        this.nameVkField = nameVkField;
        this.typeVkField = typeVkField;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTypeVkField(String typeVkField) {
        this.typeVkField = typeVkField;
    }
}
