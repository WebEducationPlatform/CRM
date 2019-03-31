package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.EmailExtra;
import com.ewp.crm.repository.interfaces.EmailExtraDAO;
import com.ewp.crm.service.interfaces.EmailExtraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailExtraServiceImpl extends CommonServiceImpl<EmailExtra> implements EmailExtraService {

    private EmailExtraDAO emailExtraDAO;

    @Autowired
    public EmailExtraServiceImpl(EmailExtraDAO emailExtraDAO) {
        this.emailExtraDAO = emailExtraDAO;
    }

    @Override
    public List<EmailExtra> getAllEmailsExtraByClient(Client client) {
        return emailExtraDAO.getAllByClient(client);
    }


}
