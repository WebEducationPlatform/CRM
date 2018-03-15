package com.ewp.crm.models;

import java.sql.Date;

public class FilteringCondition {

    private Client.Sex sex;

    private Integer ageFrom;

    private Integer ageTo;

    private String cameFrom;

    private String city;

    private String country;

    private Date dateFrom;

    private Date dateTo;

    private Client.State state;

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

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Client.State getState() {
        return state;
    }

    public void setState(Client.State state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
