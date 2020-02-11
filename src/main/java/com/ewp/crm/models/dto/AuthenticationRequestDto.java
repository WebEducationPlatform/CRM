package com.ewp.crm.models.dto;

public class AuthenticationRequestDto {
    private String email;
    private String password;

    public AuthenticationRequestDto() {
    }

    public AuthenticationRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Login: " + email + " Password: " + password;
    }
}
