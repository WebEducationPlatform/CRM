package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.dto.MailDto;

import java.util.List;

public interface MailReceiverService {

    List<MailDto> getAllUnreadEmailsFor(Long userId);

    List<Long> checkMessagesInGMailInbox();
}
