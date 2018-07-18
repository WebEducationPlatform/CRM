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
    private boolean isLiveStreamNotInAction = true;
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

    public void handleYoutubeLiveChatMessages() {
        String videoId = searchLive.getVideoIdByChannelId(apiKey, channelId);

        if (videoId != null) {
            isLiveStreamNotInAction = false;
            logger.info("Live stream is in action");
            listLive.getNamesAndMessagesFromYoutubeLiveStreamByVideoId(apiKey, videoId);
        }
    }

    public boolean isLiveStreamNotInAction() {
        return isLiveStreamNotInAction;
    }

    public void setLiveStreamNotInAction(boolean liveStreamNotInAction) {
        isLiveStreamNotInAction = liveStreamNotInAction;
    }
}
