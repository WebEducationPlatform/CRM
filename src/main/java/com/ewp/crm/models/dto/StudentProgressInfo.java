package com.ewp.crm.models.dto;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentProgressInfo that = (StudentProgressInfo) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(course, that.course) &&
                Objects.equals(module, that.module) &&
                Objects.equals(chapter, that.chapter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, course, module, chapter);
    }
}