package com.ewp.crm.service.youtube;

import com.ewp.crm.models.YoutubeClient;
import com.ewp.crm.models.YoutubeClientMessage;
import com.ewp.crm.service.interfaces.YoutubeClientMessageService;
import com.ewp.crm.service.interfaces.YoutubeClientService;
import com.ewp.crm.service.interfaces.YoutubeService;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class ListLiveChatMessages {
    private static final String LIVE_CHAT_FIELDS =
            "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,"
                    + "profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt)),"
                    + "nextPageToken,pollingIntervalMillis";
    private YouTube youtube;
    private YoutubeClientService youtubeClientService;
    private YoutubeClientMessageService youtubeClientMessageService;
    private static Logger logger = LoggerFactory.getLogger(ListLiveChatMessages.class);
    private YoutubeService youtubeService;

    @Autowired
    public ListLiveChatMessages(YoutubeClientService youtubeClientService, YoutubeClientMessageService youtubeClientMessageService, YoutubeService youtubeService) {
        this.youtubeClientService = youtubeClientService;
        this.youtubeClientMessageService = youtubeClientMessageService;
        this.youtubeService = youtubeService;
    }

    public void getNamesAndMessagesFromYoutubeLiveStreamByVideoId(String apiKey, String videoId) {

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setYouTubeRequestInitializer(new YouTubeRequestInitializer(apiKey)).build();

            // Get the liveChatId
            String liveChatId = getLiveChatId(youtube, videoId);
            if (liveChatId != null) {
                logger.info("Live chat id: " + liveChatId);
            } else {
                logger.error("Unable to find a live chat id");
                System.exit(1);
            }

            // Get live chat messages
            listChatMessages(liveChatId, null, 0);
        } catch (GoogleJsonResponseException e) {
            logger.error("GoogleJsonResponseException code: ", e);

        } catch (IOException e) {
            logger.error("IOException: ", e);

        }
    }

    /**
     * Lists live chat messages, polling at the server supplied interval. Owners and moderators of a
     * live chat will poll at a faster rate.
     *
     * @param liveChatId    The live chat id to list messages from.
     * @param nextPageToken The page token from the previous request, if any.
     * @param delayMs       The delay in milliseconds before making the request.
     */
    private void listChatMessages(
            final String liveChatId,
            final String nextPageToken,
            long delayMs) {
        Timer pollTimer = new Timer();
        pollTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            // Get chat messages from YouTube
                            LiveChatMessageListResponse response = youtube
                                    .liveChatMessages()
                                    .list(liveChatId, "snippet, authorDetails")
                                    .setPageToken(nextPageToken)
                                    .setFields(LIVE_CHAT_FIELDS)
                                    .execute();

                            // Display messages and super chat details
                            List<LiveChatMessage> messages = response.getItems();
                            for (int i = 0; i < messages.size(); i++) {
                                LiveChatMessage message = messages.get(i);
                                LiveChatMessageSnippet snippet = message.getSnippet();
                                addYoutubeClientToDB(message.getAuthorDetails().getDisplayName(), snippet.getDisplayMessage());
                            }

                            // Request the next page of messages
                            listChatMessages(
                                    liveChatId,
                                    response.getNextPageToken(),
                                    response.getPollingIntervalMillis());
                        } catch (IOException e) {
                            logger.error("Youtube Live stream is not in action any more", e);
                            youtubeService.setLiveStreamNotInAction(true);
                        }
                    }
                }, delayMs);
    }

    private void addYoutubeClientToDB(String name, String message) {
        YoutubeClient youtubeClient = youtubeClientService.findByName(name);

        if (youtubeClient != null) {
            YoutubeClientMessage youtubeClientMessage = new YoutubeClientMessage(youtubeClient, message);
            List<YoutubeClientMessage> messages = new ArrayList<>();
            messages.add(youtubeClientMessage);
            youtubeClientService.update(youtubeClient);
            youtubeClient.setMessages(messages);
            youtubeClientMessageService.add(youtubeClientMessage);
            logger.info("YoutubeClient with name{} has been updated from Youtube", youtubeClient.getFullName());
        } else {
            YoutubeClient newYoutubeClient = new YoutubeClient();
            newYoutubeClient.setFullName(name);
            YoutubeClientMessage youtubeClientMessage = new YoutubeClientMessage(message);
            List<YoutubeClientMessage> messages = new ArrayList<>();
            messages.add(youtubeClientMessage);
            newYoutubeClient.setMessages(messages);
            youtubeClientService.add(newYoutubeClient);
            youtubeClientMessage.setYoutubeClient(newYoutubeClient);
            youtubeClientMessageService.add(youtubeClientMessage);
            logger.info("YoutubeClient with name{} has been added from Youtube", newYoutubeClient.getFullName());
        }
    }

    private String getLiveChatId(YouTube youtube, String videoId) throws IOException {
        // Get liveChatId from the video
        YouTube.Videos.List videoList = youtube.videos()
                .list("liveStreamingDetails")
                .setFields("items/liveStreamingDetails/activeLiveChatId")
                .setId(videoId);
        VideoListResponse response = videoList.execute();
        for (Video v : response.getItems()) {
            String liveChatId = v.getLiveStreamingDetails().getActiveLiveChatId();
            if (liveChatId != null && !liveChatId.isEmpty()) {
                return liveChatId;
            }
        }

        return null;
    }
}
