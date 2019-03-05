package com.ewp.crm.service.impl;

import com.ewp.crm.models.ClientFeedback;
import com.ewp.crm.repository.interfaces.ClientFeedbackRepository;
import com.ewp.crm.service.interfaces.ClientFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientFeedbackServiceImpl implements ClientFeedbackService {

    private final ClientFeedbackRepository clientFeedbackRepository;

    @Autowired
    public ClientFeedbackServiceImpl(ClientFeedbackRepository clientFeedbackRepository) {
        this.clientFeedbackRepository = clientFeedbackRepository;
    }

    @Override
    public ClientFeedback addFeedback(ClientFeedback feedback) {
        return clientFeedbackRepository.saveAndFlush(feedback);
    }

    @Override
    public ClientFeedback createFeedback(String socialurl, String text, String videoUrl) {
        return new ClientFeedback(socialurl,text,videoUrl);
    }
}
