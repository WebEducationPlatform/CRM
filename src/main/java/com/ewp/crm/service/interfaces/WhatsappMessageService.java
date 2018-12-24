package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.whatsapp.WhatsappMessage;

import java.util.List;


public interface WhatsappMessageService {

    List<WhatsappMessage> findTop40BySeenFalseAndClient_IdOrderByTimeDesc(long clientId);

    long countAllBySeenFalseAndClient_Id(long clientId);

    WhatsappMessage findByMessageNumber(long messageNumber);

    List<WhatsappMessage> findAllByClient_Id(long clientId);

    WhatsappMessage save(WhatsappMessage whatsappMessage);

    WhatsappMessage findTopByClient_IdOrderByTimeDesc(long clientId);
}
