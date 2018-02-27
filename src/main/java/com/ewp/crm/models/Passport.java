package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Arrays;

@Entity
@Table(name = "passport")
public class Passport implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String series;

    private String number;

    private Date dateOfIssue;

    private String issuedBy;

    private String registration;

    @Lob
    private byte[] photoOfTheMainPage;

    @Lob
    private byte[] photoOfResidencePermit;

    public Passport() {
    }

    public Passport(String series, String number, Date dateOfIssue, String issuedBy, String registration, byte[] photoOfTheMainPage, byte[] photoOfResidencePermit) {
        this.series = series;
        this.number = number;
        this.dateOfIssue = dateOfIssue;
        this.issuedBy = issuedBy;
        this.registration = registration;
        this.photoOfTheMainPage = photoOfTheMainPage;
        this.photoOfResidencePermit = photoOfResidencePermit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(Date dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public byte[] getPhotoOfTheMainPage() {
        return photoOfTheMainPage;
    }

    public void setPhotoOfTheMainPage(byte[] photoOfTheMainPage) {
        this.photoOfTheMainPage = photoOfTheMainPage;
    }

    public byte[] getPhotoOfResidencePermit() {
        return photoOfResidencePermit;
    }

    public void setPhotoOfResidencePermit(byte[] photoOfResidencePermit) {
        this.photoOfResidencePermit = photoOfResidencePermit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Passport passport = (Passport) o;

        if (!id.equals(passport.id)) return false;
        if (!series.equals(passport.series)) return false;
        if (!number.equals(passport.number)) return false;
        if (!dateOfIssue.equals(passport.dateOfIssue)) return false;
        if (!issuedBy.equals(passport.issuedBy)) return false;
        if (!registration.equals(passport.registration)) return false;
        if (!Arrays.equals(photoOfTheMainPage, passport.photoOfTheMainPage)) return false;
        return Arrays.equals(photoOfResidencePermit, passport.photoOfResidencePermit);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + series.hashCode();
        result = 31 * result + number.hashCode();
        result = 31 * result + dateOfIssue.hashCode();
        result = 31 * result + issuedBy.hashCode();
        result = 31 * result + registration.hashCode();
        result = 31 * result + Arrays.hashCode(photoOfTheMainPage);
        result = 31 * result + Arrays.hashCode(photoOfResidencePermit);
        return result;
    }
}
