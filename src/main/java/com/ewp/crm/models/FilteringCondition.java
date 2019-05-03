package com.ewp.crm.models;

import java.time.LocalDate;

public class FilteringCondition {

    private Client.Sex sex;

    private Integer ageFrom;

    private Integer ageTo;

    private String cameFrom;

    private String city;

    private String country;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private Status status;

    private String selected;

    private int pageNumber;

    public FilteringCondition() {
    }

    public Client.Sex getSex() {
        return sex;
    }

    public void setSex(Client.Sex sex) {
        this.sex = sex;
    }

    public Integer getAgeFrom() {
        return ageFrom;
    }

    public void setAgeFrom(Integer ageFrom) {
        this.ageFrom = ageFrom;
    }

    public Integer getAgeTo() {
        return ageTo;
    }

    public void setAgeTo(Integer ageTo) {
        this.ageTo = ageTo;
    }

    public String getCameFrom() {
        return cameFrom;
    }

    public void setCameFrom(String cameFrom) {
        this.cameFrom = cameFrom;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
