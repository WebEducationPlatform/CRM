package com.ewp.crm.service.impl;

import com.ewp.crm.models.Passport;
import com.ewp.crm.repository.interfaces.PassportDAO;
import com.ewp.crm.service.interfaces.PassportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class PassportServiceImpl extends CommonServiceImpl<Passport> implements PassportService {

    private PassportDAO passportDAO;

    @Autowired
    public PassportServiceImpl(PassportDAO passportDAO) {
        this.passportDAO = passportDAO;
    }

    @Override
    public Passport encode(Passport passport) {
        passport.setSeries(encode(passport.getSeries()));
        passport.setNumber(encode(passport.getSeries()));
        passport.setIssuedBy(encode(passport.getIssuedBy()));
        passport.setRegistration(encode(passport.getRegistration()));
        return passport;
    }

    @Override
    public Passport decode(Passport passport) {
        passport.setSeries(decode(passport.getSeries()));
        passport.setNumber(decode(passport.getSeries()));
        passport.setIssuedBy(decode(passport.getIssuedBy()));
        passport.setRegistration(decode(passport.getRegistration()));
        return passport;
    }

    private String encode(String elem) {
        return Base64.getEncoder().withoutPadding().encodeToString(elem.getBytes());
    }

    private String decode(String elem) {
        return new String(Base64.getDecoder().decode(elem));
    }
}
