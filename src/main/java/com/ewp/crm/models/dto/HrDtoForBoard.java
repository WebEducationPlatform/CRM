package com.ewp.crm.models.dto;

import com.ewp.crm.models.User;

public class HrDtoForBoard {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String sex;
    private String city;
    private String country;
    private Long numberOfCards;
    private Long numberOfCalls;
    private Long avgCallsPerDay;

    public HrDtoForBoard() {
    }

    public HrDtoForBoard(final User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.sex = user.getSex();
        this.city = user.getCity();
        this.country = user.getCountry();
        this.numberOfCards = (long) user.getClients().size();
        Long numberOfCalls = (long) user.getCallRecords().size();
        this.numberOfCalls = numberOfCalls;
        long numberOfDays = user.getCallRecords().stream().map(c -> c.getDate().toLocalDate()).distinct().count();
        this.avgCallsPerDay = numberOfDays == 0L ? 0L : numberOfCalls / numberOfDays;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getNumberOfCards() {
        return numberOfCards;
    }

    public void setNumberOfCards(Long numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    public Long getNumberOfCalls() {
        return numberOfCalls;
    }

    public void setNumberOfCalls(Long numberOfCalls) {
        this.numberOfCalls = numberOfCalls;
    }

    public Long getAvgCallsPerDay() {
        return avgCallsPerDay;
    }

    public void setAvgCallsPerDay(Long avgCallsPerDay) {
        this.avgCallsPerDay = avgCallsPerDay;
    }

}
