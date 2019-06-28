package com.ewp.crm.models.dto;

import com.ewp.crm.models.User;

public class MentorDtoForMentorsPage {
    private Long id;
    private String email;

    public MentorDtoForMentorsPage(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
