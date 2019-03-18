package com.ewp.crm.models;

public class ContractFormData {

    private String inputName;
    private String inputBirthday;
    private String inputEmail;
    private String inputPhoneNumber;

    private Passport passportData;

    public Passport getPassportData() {
        return passportData;
    }

    public void setPassportData(Passport passportData) {
        this.passportData = passportData;
    }

    public ContractFormData() {
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public String getInputBirthday() {
        return inputBirthday;
    }

    public void setInputBirthday(String inputBirthday) {
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
}
