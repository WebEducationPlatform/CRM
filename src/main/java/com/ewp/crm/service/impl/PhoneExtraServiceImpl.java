package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.PhoneExtra;
import com.ewp.crm.repository.interfaces.PhoneExtraDAO;
import com.ewp.crm.service.interfaces.PhoneExtraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneExtraServiceImpl extends CommonServiceImpl<PhoneExtra> implements PhoneExtraService {

    private PhoneExtraDAO phoneExtraDAO;

    @Autowired
    public PhoneExtraServiceImpl(PhoneExtraDAO phoneExtraDAO) {
        this.phoneExtraDAO = phoneExtraDAO;
    }

    @Override
    public List<PhoneExtra> getAllPhonesExtraByClient(Client client) {
        return phoneExtraDAO.getAllByClient(client);
    }


}
