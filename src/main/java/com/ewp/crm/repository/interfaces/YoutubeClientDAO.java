package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.YoutubeClient;

        import java.util.List;

public interface YoutubeClientDAO extends CommonGenericRepository<YoutubeClient> {

    List<YoutubeClient> findAll();

    YoutubeClient findByFullName(String name);
}
