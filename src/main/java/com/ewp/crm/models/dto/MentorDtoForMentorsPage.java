package com.ewp.crm.models.dto;


public interface MentorDtoForMentorsPage {

    long getUser_Id();

    String getEmail();

    class MentorDto{
        private long id;
        private String email;

        public MentorDto(long id, String email) {
            this.id = id;
            this.email = email;
        }

        public long getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }
    }
}


