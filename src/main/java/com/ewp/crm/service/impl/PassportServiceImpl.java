package com.ewp.crm.service.impl;

import com.ewp.crm.models.Passport;
import com.ewp.crm.repository.interfaces.PassportDAO;
import com.ewp.crm.service.interfaces.PassportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PassportServiceImpl extends CommonServiceImpl<Passport> implements PassportService {
    private PassportDAO passportDAO;

    @Autowired
    public PassportServiceImpl(PassportDAO passportDAO) {
        this.passportDAO = passportDAO;
    }
}
