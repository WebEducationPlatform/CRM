package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.YoutubeConfig;
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
    private String channelId;
    private final SearchLive searchLive;
    private final ListLiveChatMessages listLive;
    private static Logger logger = LoggerFactory.getLogger(YoutubeServiceImpl.class);

    @Autowired
    public YoutubeServiceImpl(YoutubeConfig youtubeConfig, SearchLive searchLive, ListLiveChatMessages listLive) {
        apiKey = youtubeConfig.getApiKey();
        channelId = youtubeConfig.getChannelId();
        this.searchLive = searchLive;
        this.listLive = listLive;
    }

    public boolean checkLiveStreamStatus() {
        return listLive.isLiveStreamInAction();
    }

    public void handleYoutubeLiveChatMessages() {
        try {
            String videoId = searchLive.getListOfLiveStreamByChannelId(apiKey, channelId).get(0);

            if (videoId != null) {
                listLive.getNamesAndMessagesFromYoutubeLiveStreamByVideoId(apiKey, videoId);
                logger.info("Live stream is in action");
            }
        } catch (IndexOutOfBoundsException e) {
            logger.info("Live events in Youtube channel don't exist");
            }
    }
}
