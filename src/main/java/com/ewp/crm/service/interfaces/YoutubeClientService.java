package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.YouTubeTrackingCard;
import com.ewp.crm.models.YoutubeClient;

import java.util.List;
import java.util.Optional;

public interface YoutubeClientService {

    void add(YoutubeClient youtubeClient);

    List<YoutubeClient> getAll();

    List<YoutubeClient> getAllByChecked(boolean checked);

    List<YoutubeClient> getAllByYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard);

    Optional<YoutubeClient> getClientByName(String name);

    void update(YoutubeClient youtubeClient);
}
