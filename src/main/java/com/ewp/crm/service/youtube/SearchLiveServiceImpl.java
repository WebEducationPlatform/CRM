package com.ewp.crm.service.youtube;

import com.ewp.crm.service.interfaces.SearchLiveService;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class SearchLiveServiceImpl implements SearchLiveService {

    private static Logger logger = LoggerFactory.getLogger(SearchLiveService.class);

    @Override
    public List<String> getListOfLiveStreamByChannelId(String apiKey, String channelId) {

        List<String> stringList = new ArrayList<>();
        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet");
            parameters.put("eventType", "live");
            parameters.put("maxResults", "25");
            parameters.put("channelId", channelId);
            parameters.put("type", "video");

            YouTube.Search.List searchListLiveEventsRequest = youtube.search().list(parameters.get("part"));
            searchListLiveEventsRequest.setKey(apiKey);
            if (parameters.containsKey("eventType") && parameters.get("eventType") != "") {
                searchListLiveEventsRequest.setEventType(parameters.get("eventType"));
            }

            if (parameters.containsKey("maxResults")) {
                searchListLiveEventsRequest.setMaxResults(Long.parseLong(parameters.get("maxResults")));
            }

            if (parameters.containsKey("channelId") && parameters.get("channelId") != "") {
                searchListLiveEventsRequest.setChannelId(parameters.get("channelId"));
            }

            if (parameters.containsKey("type") && parameters.get("type") != "") {
                searchListLiveEventsRequest.setType(parameters.get("type"));
            }

            SearchListResponse response = searchListLiveEventsRequest.execute();
            stringList = getResults(response);

        } catch (GoogleJsonResponseException e) {
            logger.error("There was a service error: ", e);
        } catch (IOException e) {
            logger.error("Live events in Youtube channel problem with execute", e);
        }
        return stringList;
    }

    private List<String> getResults(SearchListResponse searchResponse) {
        List<String> urls = new ArrayList<>();

        List<SearchResult> searchResultList = searchResponse.getItems();
        searchResultList.stream().forEach((sr) -> {
            urls.add(sr.getId().getVideoId());
        });

        return urls;
    }
}
