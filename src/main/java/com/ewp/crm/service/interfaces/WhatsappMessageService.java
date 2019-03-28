package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.whatsapp.WhatsappMessage;

import java.util.List;
import java.util.Optional;


public interface WhatsappMessageService {

    Optional<WhatsappMessage> findById(String id);

    List<WhatsappMessage> findTop40BySeenFalseAndClient_IdOrderByTimeDesc(long clientId);

    long countAllBySeenFalseAndClient_Id(long clientId);

    Optional<WhatsappMessage> findByMessageNumber(long messageNumber);

    List<WhatsappMessage> findAllByClient_Id(long clientId);

    Optional<WhatsappMessage> save(WhatsappMessage whatsappMessage);

    Optional<WhatsappMessage> findTopByClient_IdAndSeenTrueAndFromMeTrueOrderByTimeDesc(long clientId);

    List<WhatsappMessage> findAllBySeenFalseAndFromMeFalse();

    List<WhatsappMessage> saveAll(List<WhatsappMessage> newWhatsappMessages);

    Optional<WhatsappMessage> findTopByFromMeTrueOrderByMessageNumberDesc();
}
