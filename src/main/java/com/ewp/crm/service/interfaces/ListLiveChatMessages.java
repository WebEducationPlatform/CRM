package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.YouTubeTrackingCard;

public interface ListLiveChatMessages {
    void getNamesAndMessagesFromYoutubeLiveStreamByVideoId(String apiKey, String videoId, YouTubeTrackingCard youTubeTrackingCard);
}
