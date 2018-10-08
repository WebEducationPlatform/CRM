package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.YoutubeConfig;
import com.ewp.crm.models.YouTubeTrackingCard;
import com.ewp.crm.service.interfaces.YoutubeService;
import com.ewp.crm.service.youtube.ListLiveChatMessages;
import com.ewp.crm.service.youtube.SearchLive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YoutubeServiceImpl implements YoutubeService {

    private String apiKey;
    private final SearchLive searchLive;
    private final ListLiveChatMessages listLive;
    private static Logger logger = LoggerFactory.getLogger(YoutubeServiceImpl.class);

    @Autowired
    public YoutubeServiceImpl(YoutubeConfig youtubeConfig, SearchLive searchLive, ListLiveChatMessages listLive) {
        this.apiKey = youtubeConfig.getApiKey();
        this.searchLive = searchLive;
        this.listLive = listLive;
    }

    public void handleYoutubeLiveChatMessages(YouTubeTrackingCard youTubeTrackingCard) {
        try {
            String videoId = searchLive.getListOfLiveStreamByChannelId(apiKey, youTubeTrackingCard.getYouTubeChannelID()).get(0);

            if (videoId != null) {
                listLive.getNamesAndMessagesFromYoutubeLiveStreamByVideoId(apiKey, videoId, youTubeTrackingCard);
                logger.info(youTubeTrackingCard.getChannelName() + ": Live stream is in action");
            }
        } catch (IndexOutOfBoundsException e) {
            logger.error(youTubeTrackingCard.getChannelName() + ": Live events in Youtube channel don't exist");
            }
    }
}
