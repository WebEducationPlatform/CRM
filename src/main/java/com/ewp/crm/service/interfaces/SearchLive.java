package com.ewp.crm.service.interfaces;

import java.util.List;

public interface SearchLive {
    List<String> getListOfLiveStreamByChannelId(String apiKey, String channelId);
}
