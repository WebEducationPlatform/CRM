package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * Форма заявки из vk
 */
@Entity
@Table(name = "vk_request_form")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VkRequestForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Номер поля
     */
    @Column(name = "number_vk_field")
    private Integer numberVkField;

    /**
     * Название поля
     */
    @Column(name = "name_vk_field")
    private String nameVkField;

    /**
     * Тип поля, Контактные данные или дополнительная информация
     */
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

    public VkRequestForm(Integer numberVkField, String nameVkField, String typeVkField) {
        this.numberVkField = numberVkField;
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
