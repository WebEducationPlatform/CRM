package com.ewp.crm.service.interfaces;

public interface YoutubeService {

    void handleYoutubeLiveChatMessages();

    boolean isLiveStreamNotInAction();

    void setLiveStreamNotInAction(boolean liveStreamNotInAction);
}
