package com.ewp.crm.service.impl;

import com.ewp.crm.models.ClientFeedback;
import com.ewp.crm.repository.interfaces.ClientFeedbackRepository;
import com.ewp.crm.service.interfaces.ClientFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClientFeedbackServiceImpl implements ClientFeedbackService {

    private final ClientFeedbackRepository clientFeedbackRepository;

    @Autowired
    public ClientFeedbackServiceImpl(ClientFeedbackRepository clientFeedbackRepository) {
        this.clientFeedbackRepository = clientFeedbackRepository;
    }

    @Override
    public Optional<ClientFeedback> addFeedback(ClientFeedback feedback) {
        return Optional.of(clientFeedbackRepository.saveAndFlush(feedback));
    }

    @Override
    public ClientFeedback createFeedback(String socialUrl, String text, String videoUrl) {
        return new ClientFeedback(socialUrl,text,videoUrl);
    }

    @Override
    public Optional<List<ClientFeedback>> getAllByClientId(Long id) {
        return Optional.ofNullable(clientFeedbackRepository.getAllByClientId(id));
    }

    @Override
    public Optional<List<ClientFeedback>> getAllFeedback() {
        return Optional.ofNullable(clientFeedbackRepository.findAll());
    }

    @Override
    public void deleteFeedback(Long id) {
        clientFeedbackRepository.deleteById(id);
    }

    @Override
    public void updateFeedback(ClientFeedback feedback) {
        ClientFeedback newFeedback = clientFeedbackRepository.getClientFeedbackById(feedback.getId());
        newFeedback.setSocialUrl(feedback.getSocialUrl());
        newFeedback.setText(feedback.getText());
        newFeedback.setVideoUrl(feedback.getVideoUrl());
        clientFeedbackRepository.saveAndFlush(newFeedback);
    }
}
