package com.ewp.crm.service.impl;

import com.ewp.crm.models.YoutubeClient;
import com.ewp.crm.repository.interfaces.YoutubeClientDAO;
import com.ewp.crm.service.interfaces.YoutubeClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YoutubeClientServiceImpl implements YoutubeClientService {
    private final YoutubeClientDAO youtubeClientDAO;

    @Autowired
    public YoutubeClientServiceImpl(YoutubeClientDAO youtubeClientDAO) {
        this.youtubeClientDAO = youtubeClientDAO;
    }

    @Override
    public void add(YoutubeClient youtubeClient) {
        youtubeClientDAO.saveAndFlush(youtubeClient);
    }

    @Override
    public List<YoutubeClient> findAll() {
        return youtubeClientDAO.findAll();
    }

    @Override
    public YoutubeClient findByName(String name) {
        return youtubeClientDAO.findByFullName(name);
    }

    @Override
    public void update(YoutubeClient youtubeClient) {
        youtubeClientDAO.saveAndFlush(youtubeClient);
    }
}
