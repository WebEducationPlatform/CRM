package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.whatsapp.WhatsappMessage;

import java.util.List;

public interface WhatsappMessageRepository extends CommonGenericRepository<WhatsappMessage> {
    List<WhatsappMessage> findAllByisRead(boolean isRead);
}
