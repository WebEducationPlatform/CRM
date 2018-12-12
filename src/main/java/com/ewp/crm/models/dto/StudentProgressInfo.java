package com.ewp.crm.models.dto;

public class StudentProgressInfo {
    private String email;
    private String course;
    private String module;
    private String chapter;

    public StudentProgressInfo() {
    }

    public StudentProgressInfo(String email, String course, String module, String chapter) {
        this.email = email;
        this.course = course;
        this.module = module;
        this.chapter = chapter;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }

    public String getModule() {
        return module;
    }

    public String getChapter() {
        return chapter;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    @Override
    public String toString() {
        return "StudentProgressInfo{" +
                "email='" + email + '\'' +
                ", course='" + course + '\'' +
                ", module='" + module + '\'' +
                ", chapter='" + chapter + '\'' +
                '}';
    }
}