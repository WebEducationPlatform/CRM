package com.ewp.crm.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ContractDataForm {

    private String inputFirstName;
    private String inputMiddleName;
    private String inputLastName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate inputBirthday;
    private String inputEmail;
    private String inputPhoneNumber;
    private Passport passportData;

    public ContractDataForm() {
    }

    public String getInputFirstName() {
        return inputFirstName;
    }

    public void setInputFirstName(String inputFirstName) {
        this.inputFirstName = inputFirstName;
    }

    public String getInputMiddleName() {
        return inputMiddleName;
    }

    public void setInputMiddleName(String inputMiddleName) {
        this.inputMiddleName = inputMiddleName;
    }

    public String getInputLastName() {
        return inputLastName;
    }

    public void setInputLastName(String inputLastName) {
        this.inputLastName = inputLastName;
    }

    public LocalDate getInputBirthday() {
        return inputBirthday;
    }

    public void setInputBirthday(LocalDate inputBirthday) {
        this.inputBirthday = inputBirthday;
    }

    public String getInputEmail() {
        return inputEmail;
    }

    public void setInputEmail(String inputEmail) {
        this.inputEmail = inputEmail;
    }

    public String getInputPhoneNumber() {
        return inputPhoneNumber;
    }

    public void setInputPhoneNumber(String inputPhoneNumber) {
        this.inputPhoneNumber = inputPhoneNumber;
    }

    public Passport getPassportData() {
        return passportData;
    }

    public void setPassportData(Passport passportData) {
        this.passportData = passportData;
    }
}
