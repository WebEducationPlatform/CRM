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
    private boolean isLiveStreamInAction = false;
    private YoutubeClientService youtubeClientService;
    private YoutubeClientMessageService youtubeClientMessageService;
    private static Logger logger = LoggerFactory.getLogger(ListLiveChatMessages.class);

    @Autowired
    public ListLiveChatMessages(YoutubeClientService youtubeClientService, YoutubeClientMessageService youtubeClientMessageService) {
        this.youtubeClientService = youtubeClientService;
        this.youtubeClientMessageService = youtubeClientMessageService;
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
                isLiveStreamInAction = true;
                logger.info("Live chat id: " + liveChatId);
            } else {
                isLiveStreamInAction = false;
                logger.error("Unable to find a live chat id");
            }

            // Get live chat messages
            listChatMessages(liveChatId, null, 100);
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

                            // Iterate messages and add it to DB
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
                        } catch (GoogleJsonResponseException e) {
                            isLiveStreamInAction = false;
                            logger.error("Youtube Live stream is not in action any more", e);
                        } catch (IOException e) {
                            logger.error("Failed to get list of live names and messages", e);
                        }
                    }
                }, delayMs);
    }

    private void addYoutubeClientToDB(String name, String message) {
        YoutubeClient youtubeClient = youtubeClientService.findByName(name);
        String clearMessage = clearYoutubeMessageOfEmoji(message);

        if (youtubeClient != null) {
            YoutubeClientMessage youtubeClientMessage = new YoutubeClientMessage(youtubeClient, clearMessage);
            List<YoutubeClientMessage> messages = new ArrayList<>();
            messages.add(youtubeClientMessage);
            youtubeClientService.update(youtubeClient);
            youtubeClient.setMessages(messages);
            youtubeClientMessageService.add(youtubeClientMessage);
            logger.info("YoutubeClient with name{} has been updated from Youtube", youtubeClient.getFullName());
        } else {
            YoutubeClient newYoutubeClient = new YoutubeClient();
            newYoutubeClient.setFullName(name);
            YoutubeClientMessage youtubeClientMessage = new YoutubeClientMessage(clearMessage);
            List<YoutubeClientMessage> messages = new ArrayList<>();
            messages.add(youtubeClientMessage);
            newYoutubeClient.setMessages(messages);
            youtubeClientService.add(newYoutubeClient);
            youtubeClientMessage.setYoutubeClient(newYoutubeClient);
            youtubeClientMessageService.add(youtubeClientMessage);
            logger.info("YoutubeClient with name{} has been added from Youtube", newYoutubeClient.getFullName());
        }
    }

    /**
     * Clear all emoji from a string.
     * Taken from: https://stackoverflow.com/questions/24840667/what-is-the-regex-to-extract-all-the-emojis-from-a-string
     *
     * @param message    The message from youtube live chat.
     */
    private String clearYoutubeMessageOfEmoji(String message) {
        return message.replaceAll("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|" +
                "[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|" +
                "[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|" +
                "[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|" +
                "[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|" +
                "[\u3297\u3299]\uFE0F?|[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]" +
                "\uFE0F?|[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|" +
                "\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)", " ");
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

    public boolean isLiveStreamInAction() {
        return isLiveStreamInAction;
    }
}
