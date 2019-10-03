package com.ewp.crm.models.dto;
//{
//        "sub": "109048620758485555746",
//        "name": "Vladimir Nomokonov",
//        "given_name": "Vladimir",
//        "family_name": "Nomokonov",
//        "picture": "https://lh6.googleusercontent.com/-r5xiQs6x-kU/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3rdaqOxnSnRr6PJpggIFzl-oqa0PRg/photo.jpg",
//        "email": "vladimirnomokonov@gmail.com",
//        "email_verified": true,
//        "locale": "ru"
//        }
public class GoogleUserDTO {
    private String sub;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String email;
    private boolean email_verified;
    private String locale;

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(boolean email_verified) {
        this.email_verified = email_verified;
    }
}
