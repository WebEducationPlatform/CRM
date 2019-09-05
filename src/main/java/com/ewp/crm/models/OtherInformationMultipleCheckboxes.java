package com.ewp.crm.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "other_information_multiple")
public class OtherInformationMultipleCheckboxes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oimc_id")
    private Long id;

    @Column(name = "name_field")
    private String nameField;

    @Column(name = "value_checkbox")
    private Boolean checkboxValue;

    public OtherInformationMultipleCheckboxes() {
    }

    public OtherInformationMultipleCheckboxes(String nameField) {
        this.nameField = nameField;
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

    public Boolean getCheckboxValue() {
        return checkboxValue;
    }

    public void setCheckboxValue(Boolean checkboxValue) {
        this.checkboxValue = checkboxValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtherInformationMultipleCheckboxes that = (OtherInformationMultipleCheckboxes) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(nameField, that.nameField) &&
                Objects.equals(checkboxValue, that.checkboxValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameField, checkboxValue);
    }
}
