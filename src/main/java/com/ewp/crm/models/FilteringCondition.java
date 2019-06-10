package com.ewp.crm.models;

import java.time.LocalDate;
import java.util.List;

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

    private Integer ownerUserId;

    private List<String> selectedCheckbox;

    private String checked;

    private String filetype;

    private String delimeter;

    private int pageNumber;

    public FilteringCondition() {
    }

    public Integer getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId (Integer ownerUserId) {
        this.ownerUserId = ownerUserId;
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

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getDelimeter() {
        return delimeter;
    }

    public void setDelimeter(String delimeter) {
        this.delimeter = delimeter;
    }

    public List<String> getSelectedCheckbox() {
        return selectedCheckbox;
    }

    public void setSelectedCheckbox(List<String> selectedCheckbox) {
        this.selectedCheckbox = selectedCheckbox;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }
}