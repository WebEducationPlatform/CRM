package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.whatsapp.WhatsappMessage;

import java.util.List;
import java.util.Optional;

public interface WhatsappMessageRepository extends CommonGenericRepository<WhatsappMessage> {
    Optional<WhatsappMessage> findById(String id);

    List<WhatsappMessage> findAllByisRead(boolean isRead);
}
