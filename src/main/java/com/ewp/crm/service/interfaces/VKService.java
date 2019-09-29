package com.ewp.crm.service.interfaces;

import com.ewp.crm.configs.VKConfigImpl;
import com.ewp.crm.exceptions.parse.ParseClientException;
import com.ewp.crm.exceptions.util.VKAccessTokenException;
import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.VkProfileInfo;
import com.ewp.crm.models.conversation.ChatMessage;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VKService {
    String receivingTokenUri();

    Optional<String> getShortLinkForUrl(String url);

    Optional<List<String>> getNewMassages() throws VKAccessTokenException;

    Optional<List<ChatMessage>> getMassagesFromGroup(String userid, int count, boolean getLastReadied, boolean getNew);

    Optional<Map<Client,Integer>> getNewMassagesFromGroup();

    void sendMessageToClient(Long clientId, String templateText, String body, User principal);

    Optional<Long> getVKIdByUrl(String url);

    Optional<Client> getVkLinkById(String userID);

    void simpleVKNotification(Long clientId, String templateText);

    Optional<List<VkMember>> getAllVKMembers(Long groupId, Long offset);

    String sendMessageById(Long id, String msg);

    String sendMessageById(Long id, String msg, String token);

    void sendFirstSkypeNotification(Client client, ZonedDateTime date, VKConfigImpl.firstSkypeNotificationType type);

    void sendMessageByChatId(String id, String message);

    Optional<List<Long>> getUsersIdFromCommunityMessages();

    Optional<Client> getClientFromVkId(Long id);

    Map<String, String> getUserDataById(Long id, String additionalFields, String splitter);

    Map<String, String> getGroupDataById(Long id, String additionalFields, String splitter);

    Client parseClientFromMessage(String message) throws ParseClientException;

    String refactorAndValidateVkLink(String link);

    void setTechnicalAccountToken(String technicalAccountToken);

    String replaceApplicationTokenFromUri(String uri);

    String createNewAudience(String groupName, String idVkCabinet) throws Exception;

    void addUsersToAudience(String groupId, String contacts, String idVkCabinet) throws Exception;

    void removeUsersFromAudience(String groupId, String contacts, String idVkCabinet) throws Exception;

    String getFirstContactMessage();

    boolean hasTechnicalAccountToken();

    String getLongIDFromShortName(String vkGroupShortName);

    Optional<String> getIdFromLink(String link);

    void markAsRead(String userId, String token, String startMessageId);

    String getVkPhotoLinkByClientProfileId(String vkProfileId);

    Optional<VkProfileInfo> getProfileInfoById(long vkId);

    void fillClientFromProfileVK(Client client);

    void sendDailyAdvertisementReport(String template);

    Optional<String> getAllCountries();

    Optional<String> getCitiesByCountry(int countryId, String query);

}
