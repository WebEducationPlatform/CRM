package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.YoutubeClient;

import java.util.List;

public interface YoutubeClientService {

    void add(YoutubeClient youtubeClient);

    List<YoutubeClient> findAll();

    YoutubeClient findByName(String name);

    void update(YoutubeClient youtubeClient);
}
