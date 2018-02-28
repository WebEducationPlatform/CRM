package com.ewp.crm.service.impl;

import com.ewp.crm.models.Passport;
import com.ewp.crm.repository.interfaces.PassportDAO;
import com.ewp.crm.service.interfaces.PassportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassportServiceImpl implements PassportService {

    private PassportDAO passportDAO;

    @Autowired
    public PassportServiceImpl(PassportDAO passportDAO) {
        this.passportDAO = passportDAO;
    }

    @Override
    public void add(Passport passport) {
        passportDAO.saveAndFlush(passport);
    }

    @Override
    public void update(Passport passport) {
        passportDAO.saveAndFlush(passport);
    }

    @Override
    public List<Passport> getAll() {
        return passportDAO.findAll();
    }

    @Override
    public Passport get(Long id) {
        return passportDAO.findOne(id);
    }

    @Override
    public void delete(Passport passport) {
        passportDAO.delete(passport);
    }
}
