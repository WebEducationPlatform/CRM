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
        final Long callRecords = (long) user.getCallRecords().size();
        this.numberOfCalls = callRecords;
        long numberOfDays = user.getCallRecords().stream().map(c -> c.getDate().toLocalDate()).distinct().count();
        this.avgCallsPerDay = numberOfDays == 0L ? 0L : callRecords / numberOfDays;
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

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(final String sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public Long getNumberOfCards() {
        return numberOfCards;
    }

    public void setNumberOfCards(final Long numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    public Long getNumberOfCalls() {
        return numberOfCalls;
    }

    public void setNumberOfCalls(final Long numberOfCalls) {
        this.numberOfCalls = numberOfCalls;
    }

    public Long getAvgCallsPerDay() {
        return avgCallsPerDay;
    }

    public void setAvgCallsPerDay(final Long avgCallsPerDay) {
        this.avgCallsPerDay = avgCallsPerDay;
    }

}
