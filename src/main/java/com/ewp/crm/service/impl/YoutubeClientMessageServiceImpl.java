package com.ewp.crm.service.impl;

import com.ewp.crm.models.YoutubeClientMessage;
import com.ewp.crm.repository.interfaces.YoutubeClientMessageDAO;
import com.ewp.crm.service.interfaces.YoutubeClientMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YoutubeClientMessageServiceImpl implements YoutubeClientMessageService {
    private final YoutubeClientMessageDAO youtubeClientMessageDAO;

    @Autowired
    public YoutubeClientMessageServiceImpl(YoutubeClientMessageDAO youtubeClientMessageDAO) {
        this.youtubeClientMessageDAO = youtubeClientMessageDAO;
    }

    @Override
    public void add(YoutubeClientMessage youtubeClientMessage) {
        youtubeClientMessageDAO.saveAndFlush(youtubeClientMessage);
    }
}
