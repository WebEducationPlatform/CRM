package com.ewp.crm.models.dto;

import com.ewp.crm.models.Client;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class VkProfileInfo {
    private long id;
    private String firstName;
    private String lastName;
    private String country;
    private String city;
    private LocalDate birthdate;
    private Client.Sex sex;
    private String phone;
    private String university;

    public VkProfileInfo() {
    }

    public VkProfileInfo(long id, String firstName, String lastName, String country, String city, LocalDate birthdate, Client.Sex sex, String phone, String university) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.city = city;
        this.birthdate = birthdate;
        this.sex = sex;
        this.phone = phone;
        this.university = university;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public Client.Sex getSex() {
        return sex;
    }

    public String getPhone() {
        return phone;
    }

    public String getUniversity() {
        return university;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setSex(Client.Sex sex) {
        this.sex = sex;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VkProfileInfo that = (VkProfileInfo) o;
        return id == that.id &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(country, that.country) &&
                Objects.equals(city, that.city) &&
                Objects.equals(birthdate, that.birthdate) &&
                sex == that.sex &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(university, that.university);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, firstName, lastName, country, city, birthdate, sex, phone, university);
    }

    @Override
    public String toString() {
        return "VkProfileInfo{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", birthdate=" + birthdate +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", university='" + university + '\'' +
                '}';
    }
}