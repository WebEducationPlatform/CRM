package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.YouTubeTrackingCard;
import com.ewp.crm.models.YoutubeClient;

import java.util.List;

public interface YoutubeClientDAO extends CommonGenericRepository<YoutubeClient> {

    List<YoutubeClient> findAll();

    List<YoutubeClient> getAllByChecked(boolean checked);

    List<YoutubeClient> getAllByYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard);

    YoutubeClient getByFullName(String name);
}
