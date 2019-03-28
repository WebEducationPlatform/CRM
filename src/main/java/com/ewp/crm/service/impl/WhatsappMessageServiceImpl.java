package com.ewp.crm.service.impl;

import com.ewp.crm.models.whatsapp.WhatsappMessage;
import com.ewp.crm.repository.interfaces.WhatsappMessageRepository;
import com.ewp.crm.service.interfaces.WhatsappMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WhatsappMessageServiceImpl implements WhatsappMessageService {
    private final WhatsappMessageRepository whatsappMessageRepository;

    @Autowired
    public WhatsappMessageServiceImpl(WhatsappMessageRepository whatsappMessageRepository) {
        this.whatsappMessageRepository = whatsappMessageRepository;
    }

    @Override
    public Optional<WhatsappMessage> findById(String id) {
        return Optional.ofNullable(whatsappMessageRepository.findById(id));
    }

    @Override
    public List<WhatsappMessage> findTop40BySeenFalseAndClient_IdOrderByTimeDesc(long clientId) {
        return whatsappMessageRepository.findTop40BySeenFalseAndFromMeFalseAndClient_IdOrderByTimeDesc(clientId);
    }

    @Override
    public long countAllBySeenFalseAndClient_Id(long clientId) {
        return whatsappMessageRepository.countAllBySeenFalseAndClient_Id(clientId);
    }

    @Override
    public Optional<WhatsappMessage> findByMessageNumber(long messageNumber) {
        return Optional.ofNullable(whatsappMessageRepository.findByMessageNumber(messageNumber));
    }

    @Override
    public List<WhatsappMessage> findAllByClient_Id(long clientId) {
        return whatsappMessageRepository.findAllByClient_Id(clientId);
    }

    @Override
    public Optional<WhatsappMessage> save(WhatsappMessage whatsappMessage) {
        return Optional.of(whatsappMessageRepository.save(whatsappMessage));
    }

    @Override
    public Optional<WhatsappMessage> findTopByClient_IdAndSeenTrueAndFromMeTrueOrderByTimeDesc(long clientId) {
        return whatsappMessageRepository.findTopByClient_IdAndSeenTrueAndFromMeTrueOrderByTimeDesc(clientId);
    }

    @Override
    public List<WhatsappMessage> findAllBySeenFalseAndFromMeFalse() {
        return whatsappMessageRepository.findAllBySeenFalseAndFromMeFalse();
    }

    @Override
    public List<WhatsappMessage> saveAll(List<WhatsappMessage> newWhatsappMessages) {
        return whatsappMessageRepository.saveAll(newWhatsappMessages);
    }

    @Override
    public Optional<WhatsappMessage> findTopByFromMeTrueOrderByMessageNumberDesc() {
        return Optional.ofNullable(whatsappMessageRepository.findTopByFromMeTrueOrderByMessageNumberDesc());
    }
}
