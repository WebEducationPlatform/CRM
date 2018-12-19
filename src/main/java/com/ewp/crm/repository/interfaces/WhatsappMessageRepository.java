package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Whatsapp.WhatsappMessage;

import java.util.List;

public interface WhatsappMessageRepository extends CommonGenericRepository<WhatsappMessage>, ClientRepositoryCustom {
    List<WhatsappMessage> findAllByIsRead(boolean isRead);
}
