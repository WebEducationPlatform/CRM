package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.YoutubeConfig;
import com.ewp.crm.models.YouTubeTrackingCard;
import com.ewp.crm.service.interfaces.ListLiveChatMessagesService;
import com.ewp.crm.service.interfaces.SearchLiveService;
import com.ewp.crm.service.interfaces.YoutubeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YoutubeServiceImpl implements YoutubeService {

    private String apiKey;
    private final SearchLiveService searchLiveService;
    private final ListLiveChatMessagesService listLive;
    private static Logger logger = LoggerFactory.getLogger(YoutubeServiceImpl.class);

    @Autowired
    public YoutubeServiceImpl(YoutubeConfig youtubeConfig, SearchLiveService searchLiveService, ListLiveChatMessagesService listLive) {
        this.apiKey = youtubeConfig.getApiKey();
        this.searchLiveService = searchLiveService;
        this.listLive = listLive;
    }

    public void handleYoutubeLiveChatMessages(YouTubeTrackingCard youTubeTrackingCard) {
        try {
            String videoId = searchLiveService.getListOfLiveStreamByChannelId(apiKey, youTubeTrackingCard.getYouTubeChannelID()).get(0);

            if (videoId != null) {
                listLive.getNamesAndMessagesFromYoutubeLiveStreamByVideoId(apiKey, videoId, youTubeTrackingCard);
                logger.info(youTubeTrackingCard.getChannelName() + ": Live stream is in action");
            }
        } catch (IndexOutOfBoundsException e) {
            logger.error(youTubeTrackingCard.getChannelName() + ": Live events in Youtube channel don't exist");
            }
    }
}
