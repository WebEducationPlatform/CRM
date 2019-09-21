package com.ewp.crm.service.impl;

import com.ewp.crm.configs.VKConfigImpl;
import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.exceptions.parse.ParseClientException;
import com.ewp.crm.exceptions.util.VKAccessTokenException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.Client.Sex;
import com.ewp.crm.models.Message;
import com.ewp.crm.models.PotentialClient;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.User;
import com.ewp.crm.models.VkMember;
import com.ewp.crm.models.VkRequestForm;
import com.ewp.crm.models.YoutubeClient;
import com.ewp.crm.models.conversation.ChatMessage;
import com.ewp.crm.models.conversation.ChatType;
import com.ewp.crm.models.dto.VkProfileInfo;
import com.ewp.crm.service.interfaces.AdReportService;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.GoogleAdsService;
import com.ewp.crm.service.interfaces.MessageService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.SocialProfileService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.VKService;
import com.ewp.crm.service.interfaces.VkMemberService;
import com.ewp.crm.service.interfaces.VkRequestFormService;
import com.ewp.crm.service.interfaces.YoutubeClientService;
import com.ewp.crm.util.validators.PhoneValidator;
import com.github.scribejava.apis.VkontakteApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponseException;
import com.google.api.ads.adwords.lib.utils.ReportException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@PropertySource(value = "file:./anti-captcha.properties")
public class VKServiceImpl implements VKService {
    private static Logger logger = LoggerFactory.getLogger(VKService.class);
    private final YoutubeClientService youtubeClientService;
    private final SocialProfileService socialProfileService;
    private final ClientHistoryService clientHistoryService;
    private final ClientService clientService;
    private final MessageService messageService;
    private final UserService userService;
    private final ProjectPropertiesService projectPropertiesService;
    private final VkRequestFormService vkRequestFormService;
    private final VkMemberService vkMemberService;
    private final PhoneValidator phoneValidator;
    private final RestTemplate restTemplate;
    private final AdReportService yandexDirectAdReportService;
    private final AdReportService vkAdsReportService;
    private final GoogleAdsService googleAdsService;
    private final MessageTemplateService messageTemplateService;
    private Environment env;

    private final String vkPattern = "[^\\/]+$";// подстрока от последнего "/" до конца строки
    private final String allDigitPattern = "\\d+";
    private final String idString = "id";
    private final String zeroString = "0";
    private final String vkURL = "https://vk.com/";

    private String vkApi;
    //Токен аккаунта, отправляющего сообщения
    //Айди группы
    private String clubId;
    //Версия API ВК
    private String version;
    //Токен доступа от имени сообщества
    private String communityToken;
    //Айди приложения (Может изменяться на релизе)
    private String applicationId;
    private String display;
    //URL, на который приходит
    private String redirectUri;
    private String scope;
    private String technicalAccountToken;
    private OAuth20Service service;
    private String firstContactMessage;
    private String managerToken;
    //ID чата, в который посылается отчёт
    private final String vkReportChatId;
    private final String firstSkypeNotifyChatId;
    private final String vkAppAccessToken;

    @Value("${userKey}")
    private String userKey;
    private VKConfig vkConfig;

    @Autowired
    public VKServiceImpl(VKConfig vkConfig,
                         YoutubeClientService youtubeClientService,
                         SocialProfileService socialProfileService,
                         ClientHistoryService clientHistoryService,
                         ClientService clientService,
                         MessageService messageService,
                         UserService userService,
                         MessageTemplateService messageTemplateService,
                         ProjectPropertiesService projectPropertiesService,
                         VkRequestFormService vkRequestFormService,
                         VkMemberService vkMemberService,
                         PhoneValidator phoneValidator,
                         RestTemplate restTemplate,
                         GoogleAdsService googleAdsService,
                         @Qualifier("YandexDirect") AdReportService yandexDirectAdReportService,
                         @Qualifier("VkAds") AdReportService vkAdsReportService, MessageTemplateService messageTemplateService1,
                         Environment env) {
        this.vkConfig = vkConfig;
        clubId = vkConfig.getClubIdWithMinus();
        version = vkConfig.getVersion();
        communityToken = vkConfig.getCommunityToken();
        applicationId = vkConfig.getApplicationId();
        display = vkConfig.getDisplay();
        redirectUri = vkConfig.getRedirectUri();
        scope = vkConfig.getScope();
        vkApi = vkConfig.getVkApiUrl();
        managerToken = vkConfig.getManagerToken();
        vkReportChatId = vkConfig.getVkReportChatId();
        firstSkypeNotifyChatId = vkConfig.getFirstSkypeNotifyChatId();
        vkAppAccessToken = vkConfig.getVkAppAccessToken();
        this.youtubeClientService = youtubeClientService;
        this.socialProfileService = socialProfileService;
        this.clientHistoryService = clientHistoryService;
        this.clientService = clientService;
        this.messageService = messageService;
        this.userService = userService;
        this.projectPropertiesService = projectPropertiesService;
        this.vkRequestFormService = vkRequestFormService;
        this.vkMemberService = vkMemberService;
        this.messageTemplateService = messageTemplateService1;
        this.service = new ServiceBuilder(clubId).build(VkontakteApi.instance());
        this.firstContactMessage = vkConfig.getFirstContactMessage();
        this.phoneValidator = phoneValidator;
        this.restTemplate = restTemplate;
        this.yandexDirectAdReportService = yandexDirectAdReportService;
        this.vkAdsReportService = vkAdsReportService;
        this.googleAdsService = googleAdsService;
        this.env = env;
    }


    public HttpClient getHttpClient() {
        return HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
    }

    @Override
    public String receivingTokenUri() {
        return "https://oauth.vk.com/authorize" +
                "?client_id=" + applicationId +
                "&scope=" + scope +
                "&redirect_uri=" + redirectUri +
                "&display=" + display +
                "&v=" + version +
                "&response_type=token";
    }

    // Позволяет получить URL, сокращенный с помощью vk.cc
    @Override
    public Optional<String> getShortLinkForUrl(String url) {
        String uriGetShortLink = vkApi + "utils.getShortLink" +
                "?url=" + url +
                "&access_token=" + technicalAccountToken +
                "&v=" + version +
                "&private=1";
        try {
            HttpGet httpGetMessages = new HttpGet(uriGetShortLink);
            HttpClient httpClient = getHttpClient();
            HttpResponse response = httpClient.execute(httpGetMessages);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONObject responseObj = json.getJSONObject("response");
            if (responseObj != null) {
                String link = responseObj.optString("short_url");
                return Optional.ofNullable(link);
            }
        } catch (JSONException e) {
            logger.error("Can not read short link from JSON ", e);
        } catch (IOException e) {
            logger.error("Failed to connect to VK server ", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<String>> getNewMassages() throws VKAccessTokenException {
        logger.info("VKService: getting new messages...");
        if (technicalAccountToken == null && (technicalAccountToken = projectPropertiesService.get() != null ? projectPropertiesService.get().getTechnicalAccountToken() : null) == null) {
            throw new VKAccessTokenException(env.getProperty("messaging.vk.exception.access-token"));
        }
        String uriGetMassages = vkApi + "messages.getHistory" +
                "?user_id=" + clubId +
                "&rev=0" +
                "&v=" + version +
                "&access_token=" + technicalAccountToken;
        try {
            HttpGet httpGetMessages = new HttpGet(uriGetMassages);
            HttpClient httpClient = getHttpClient();
            HttpResponse response = httpClient.execute(httpGetMessages);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONArray jsonMessages = json.getJSONObject("response").getJSONArray("items");
            List<String> resultList = new ArrayList<>();
            for (int i = 0; i < jsonMessages.length(); i++) {
                JSONObject jsonMessage = jsonMessages.getJSONObject(i);
                if ((clubId.equals(jsonMessage.getString("from_id"))) && (jsonMessage.getInt("read_state") == 0)) {
                    String messageBody = jsonMessage.getString("body");
                    markAsRead(clubId, technicalAccountToken, jsonMessage.optString("id"));
                    if (messageBody.startsWith("Новая заявка")) {
                        resultList.add(messageBody);
                    }
                }
            }
            return Optional.of(resultList);
        } catch (JSONException e) {
            logger.error("Can not read message from JSON ", e);
        } catch (IOException e) {
            logger.error("Failed to connect to VK server ", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<ChatMessage>> getMassagesFromGroup(String userid, int count, boolean getLastReadied, boolean getNew) {
        logger.info("VKService: getting messages...");

        String uriGetMassages = vkApi + "messages.getHistory" +
                "?user_id=" + userid +
                "&group_id=" + vkConfig.getClubId() +
                "&count=" + count +
                "&rev=0" +
                "&v=" + version +
                "&access_token=" + communityToken;

        try {
            HttpGet httpGetMessages = new HttpGet(uriGetMassages);
            HttpClient httpClient = getHttpClient();
            HttpResponse response = httpClient.execute(httpGetMessages);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONArray jsonMessages = json.getJSONObject("response").getJSONArray("items");

            List<ChatMessage> resultList = new LinkedList<>();

            for (int i = 1; i < jsonMessages.length(); i++) {
                JSONObject jsonMessage = jsonMessages.getJSONObject(i);

                String id = jsonMessage.getString("id");
                String body = jsonMessage.getString("body");
                Integer read_state = jsonMessage.getInt("read_state");
                Long date = jsonMessage.getLong("date");
                Integer out = jsonMessage.getInt("out");

                ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(date * 1000), TimeZone.getDefault().toZoneId());

                ChatMessage newChatMessage = new ChatMessage(id,
                        userid,
                        ChatType.vk, body,
                        time,
                        read_state == 1,
                        out == 1);
                //если запросили последнее прочитанное сообщение клиента
                if (getLastReadied && read_state == 1 && out == 1) {
                    resultList.add(newChatMessage);
                    return Optional.of(resultList);
                }
                //если запросили только новые
                if (getNew && read_state == 0 && out == 0) {
                    resultList.add(newChatMessage);
                } else if (!getLastReadied && !getNew) {
                    resultList.add(newChatMessage);
                }
            }
            return Optional.of(resultList);
        } catch (JSONException e) {
            logger.error("Can not read message from JSON ", e);
        } catch (IOException e) {
            logger.error("Failed to connect to VK server ", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Map<Client, Integer>> getNewMassagesFromGroup() {
        logger.info("VKService: getting new messages from conversations...");

        String uriGetMassages = vkApi + "messages.getConversations" +
                "?filter=unread" +
                "&group_id=" + vkConfig.getClubId() +
                "&v=" + version +
                "&access_token=" + communityToken;
        try {
            HttpGet httpGetMessages = new HttpGet(uriGetMassages);
            HttpClient httpClient = getHttpClient();
            HttpResponse response = httpClient.execute(httpGetMessages);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);

            Map<Client, Integer> resultMap = new HashMap<>();

            if (json.optJSONObject("response") == null || json.getJSONObject("response").optJSONArray("items") == null) {
                return Optional.of(resultMap);
            }

            JSONArray jsonMessages = json.getJSONObject("response").getJSONArray("items");

            for (int i = 0; i < jsonMessages.length(); i++) {
                JSONObject object = jsonMessages.getJSONObject(i);

                Integer count = ((Integer) ((JSONObject) object.get("conversation")).get("unread_count"));
                String userId = Integer.toString((Integer) ((JSONObject) ((JSONObject) object.get("conversation")).get("peer")).get("id"));
                Optional<Client> client = getVkLinkById(userId);

                client.ifPresent(x -> resultMap.put(x, count));
            }

            return Optional.of(resultMap);

        } catch (JSONException e) {
            logger.error("Can not read message from JSON ", e);
        } catch (IOException e) {
            logger.error("Failed to connect to VK server ", e);
        }
        return Optional.empty();
    }

    @Override
    public void sendMessageToClient(Long clientId, String templateText, String body, User principal) {
        Optional<Client> client = clientService.getClientByID(clientId);
        if (client.isPresent()) {
            List<SocialProfile> socialProfiles = client.get().getSocialProfiles();
            for (SocialProfile socialProfile : socialProfiles) {
                if (socialProfile.getSocialNetworkType().getName().equals("vk")) {
                    Long id = Long.parseLong(socialProfile.getSocialId());
                    String vkText = messageTemplateService.prepareText(client.get(), templateText, body);
                    String token = communityToken;
                    if (principal != null) {
                        User user = userService.get(principal.getId());
                        if (user.getVkToken() != null) {
                            token = user.getVkToken();
                        }
                        Optional<Message> message = messageService.addMessage(Message.Type.VK, vkText);
                        if (message.isPresent()) {
                            clientHistoryService.createHistory(principal, client.get(), message.get()).ifPresent(client.get()::addHistory);
                            clientService.updateClient(client.get());
                        }
                    }
                    sendMessageById(id, vkText, token);
                }
            }
        }
    }

    /**
     * Get user VK id by profile url.
     *
     * @param url user profile url.
     * @return optional of user VK id.
     */
    @Override
    public Optional<Long> getVKIdByUrl(String url) {
        url = url.trim();
        Optional<Long> result = Optional.empty();
        if (url.matches("(.*)vk.com/id(\\d*)")) {
            result = Optional.of(Long.parseLong(url.replaceAll(".+id", "")));
        } else if (url.matches("(.*)vk.com/(.*)")) {
            String screenName = url.substring(url.lastIndexOf("/") + 1);
            String urlGetMessages = vkApi + "users.get" +
                    "?user_ids=" + screenName +
                    "&v=" + version +
                    "&access_token=" + communityToken;
            try {
                HttpGet httpGetMessages = new HttpGet(urlGetMessages);
                HttpClient httpClient = getHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGetMessages);
                String entity = EntityUtils.toString(httpResponse.getEntity());
                JSONArray users = new JSONObject(entity).getJSONArray("response");
                result = Optional.of(users.getJSONObject(0).getLong("id"));
            } catch (IOException e) {
                logger.error("Failed to connect to VK server", e);
            } catch (JSONException e) {
                logger.error("Can not read message from JSON", e);

                // todo  исправить, ошибку при открытии карточки
            }
        }
        return result;
    }

    /**
     * Send VK notification to client without logging and additional body parameters.
     *
     * @param clientId     recipient client.
     * @param templateText email template text.
     */
    @Override
    public void simpleVKNotification(Long clientId, String templateText) {
        sendMessageToClient(clientId, templateText, "", null);
    }

    @Override
    public Optional<List<VkMember>> getAllVKMembers(Long groupId, Long offset) {
        logger.info("VKService: getting all VK members...");
        if (groupId == null) {
            groupId = Long.parseLong(clubId) * (-1);
        }
        String urlGetMessages = vkApi + "groups.getMembers" +
                "?group_id=" + groupId +
                "&offset=" + offset +
                "&v=" + version +
                "&access_token=" + communityToken;
        try {
            HttpGet httpGetMessages = new HttpGet(urlGetMessages);
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpGetMessages);
            String result = EntityUtils.toString(httpResponse.getEntity());
            JSONObject json = new JSONObject(result);
            JSONObject responeJson = json.getJSONObject("response");
            JSONArray jsonArray = responeJson.getJSONArray("items");
            List<VkMember> vkMembers = new LinkedList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                VkMember vkMember = new VkMember(Long.parseLong(jsonArray.get(i).toString()), groupId);
                if (!vkMemberService.getVkMemberById(vkMember.getVkId()).isPresent()) {
                    vkMembers.add(vkMember);
                }
            }
            return Optional.of(vkMembers);
        } catch (IOException e) {
            logger.error("Failed to connect to VK server");
        } catch (JSONException e) {
            logger.error("Can not read message from JSON");
        }
        return Optional.empty();
    }

    @Override
    public String sendMessageById(Long id, String msg) {
        return sendMessageById(id, msg, communityToken);
    }

    private Optional<String> checkJsonResponseForErrors(JSONObject json) throws JSONException {
        if (json.toString().contains("Permission to perform this action is denied") | json.toString().contains("Can't send messages to this user due to their privacy settings")) {
            JSONArray jsonArray = json.getJSONObject("error").getJSONArray("request_params");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("key").equalsIgnoreCase("user_id")) {
                    return Optional.ofNullable(jsonArray.getJSONObject(i).getString("value"));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public String sendMessageById(Long id, String msg, String token) {
        logger.info("VKService: sending message to client with id {}...", id);
        String sendMsgRequest = vkApi + "messages.send";
        HttpPost request = new HttpPost(sendMsgRequest);
        List<NameValuePair> params = new ArrayList<>(4);
        params.add(new BasicNameValuePair("user_id", String.valueOf(id)));
        params.add(new BasicNameValuePair("v", version));
        params.add(new BasicNameValuePair("message", msg));
        params.add(new BasicNameValuePair("access_token", token));
        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to encode VK request", e);
        }
        HttpClient httpClient = getHttpClient();
        try {
            HttpResponse response = httpClient.execute(request);
            JSONObject jsonEntity = new JSONObject(EntityUtils.toString(response.getEntity()));
            JsonObject convertedJsonEntity = new Gson().fromJson(jsonEntity.toString(), JsonObject.class);

            Optional<String> errorString = checkJsonResponseForErrors(jsonEntity);
            if (errorString.isPresent()) {
                return errorString.get();
            }

            if (jsonEntity.toString().contains("Captcha needed")) {
                logger.info("Сaptcha solution...");

                OkHttpClient client = new OkHttpClient();
                String captchaURL = convertedJsonEntity.getAsJsonObject("error").get("captcha_img").getAsString();
                String captchaSid = convertedJsonEntity.getAsJsonObject("error").get("captcha_sid").getAsString();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                JSONObject bodyForReport = new JSONObject("{\n" +
                        "    \"clientKey\":\"" + userKey + "\",\n" +
                        "    \"task\":\n" +
                        "        {\n" +
                        "            \"type\":\"ImageToTextTask\",\n" +
                        "            \"body\":\"" + getByteArrayFromImageURL(captchaURL) + "\",\n" +
                        "            \"phrase\":false,\n" +
                        "            \"case\":false,\n" +
                        "            \"numeric\":false,\n" +
                        "            \"math\":0,\n" +
                        "            \"minLength\":0,\n" +
                        "            \"maxLength\":0\n" +
                        "        }\n" +
                        "}");

                RequestBody bodyReport = RequestBody.create(JSON, bodyForReport.toString());
                Request requestReport = new Request.Builder()
                        .url("https://api.anti-captcha.com/createTask").
                                post(bodyReport).build();
                com.squareup.okhttp.Response responseReport = client.newCall(requestReport).execute();
                JsonObject convertedObject = new Gson().fromJson(responseReport.body().string(), JsonObject.class);
                String taskId = convertedObject.get("taskId").getAsString();

                Thread.sleep(15000);

                String text;

                try {
                    text = getResultCaptcha(taskId);
                } catch (NullPointerException e) {
                    logger.error("Again get captcha result");
                    Thread.sleep(40000);
                    text = getResultCaptcha(taskId);
                }

                String sendMsgWithCaptcha = vkApi + "messages.send";

                HttpPost newRequest = new HttpPost(sendMsgWithCaptcha);
                List<NameValuePair> newParams = new ArrayList<>(6);
                newParams.add(new BasicNameValuePair("user_id", String.valueOf(id)));
                newParams.add(new BasicNameValuePair("v", version));
                newParams.add(new BasicNameValuePair("message", msg));
                newParams.add(new BasicNameValuePair("access_token", token));
                newParams.add(new BasicNameValuePair("captcha_sid", captchaSid));
                newParams.add(new BasicNameValuePair("captcha_key", text));
                try {
                    newRequest.setEntity(new UrlEncodedFormEntity(newParams, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    logger.error("Failed to encode VK request", e);
                }
                HttpResponse newResponse = httpClient.execute(newRequest);
                JSONObject newJsonEntity = new JSONObject(EntityUtils.toString(newResponse.getEntity()));
                Optional<String> newErrorString = checkJsonResponseForErrors(newJsonEntity);
                if (newErrorString.isPresent()) {
                    return newErrorString.get();
                }
                return determineResponse(newJsonEntity);
            }
            return determineResponse(jsonEntity);
        } catch (JSONException e) {
            logger.error("JSON couldn't parse response ", e);
        } catch (IOException e) {
            logger.error("Failed connect to vk api ", e);
        } catch (InterruptedException e) {
            logger.error("Failed to get captcha ", e);
        }
        return env.getProperty("messaging.vk.send-by-id-error");
    }

    @Override
    public void sendFirstSkypeNotification(Client client, ZonedDateTime date, VKConfigImpl.firstSkypeNotificationType type) {
        String template;
        switch (type) {
            case UPDATE:
                template = vkConfig.getFirstSkypeUpdateMessageTemplate();
                break;
            case DELETE:
                template = vkConfig.getFirstSkypeDeleteMessageTemplate();
                break;
            case CREATE:
            default:
                template = vkConfig.getFirstSkypeMessageTemplate();
                break;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String message = String.format(template,
                client.getName(),
                client.getLastName(),
                client.getId(),
                date.format(dateTimeFormatter));
        sendMessageByChatId(firstSkypeNotifyChatId, message);
    }

    @Override
    public void sendMessageByChatId(String id, String message) {
        String sendMsgRequest = vkApi + "messages.send";
        HttpPost request = new HttpPost(sendMsgRequest);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("random_id", String.valueOf(new Random().nextInt(32))));
        params.add(new BasicNameValuePair("chat_id", id));
        params.add(new BasicNameValuePair("message", message));
        params.add(new BasicNameValuePair("access_token", communityToken));
        params.add(new BasicNameValuePair("v", version));
        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to encode VK request", e);
        }
        HttpClient httpClient = getHttpClient();
        try {
            HttpResponse response = httpClient.execute(request);
            logger.debug("Message successfully has been sent to the dialogue");
        } catch (IOException e) {
            logger.error("Can't send message to vk chat by id " + id, e);
        }
    }

    // Determine text, which varies depending of the success of the sending message
    private String determineResponse(JSONObject jsonObject) throws JSONException {
        try {
            jsonObject.getInt("response");
            return env.getProperty("messaging.vk.send-ok");
        } catch (JSONException e) {
            JSONObject jsonError = jsonObject.getJSONObject("error");
            String errorMessage = jsonError.getString("error_msg");
            logger.error(errorMessage);
            return errorMessage;
        }
    }

    @Override
    public Optional<List<Long>> getUsersIdFromCommunityMessages() {
        logger.info("VKService: getting user ids from community messages...");
        String uriGetDialog = vkApi + "messages.getDialogs" +
                "?v=" + version +
                "&unread=1" +
                "&access_token=" +
                communityToken;

        HttpGet httpGetDialog = new HttpGet(uriGetDialog);
        HttpClient httpClient = getHttpClient();
        try {
            HttpResponse response = httpClient.execute(httpGetDialog);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONObject responseObject = json.getJSONObject("response");
            JSONArray jsonUsers = responseObject.getJSONArray("items");
            List<Long> resultList = new ArrayList<>();
            for (int i = 0; i < jsonUsers.length(); i++) {
                JSONObject jsonMessage = jsonUsers.getJSONObject(i).getJSONObject("message");
                resultList.add(jsonMessage.getLong("user_id"));
            }
            return Optional.of(resultList);
        } catch (JSONException e) {
            logger.error("Can not read message from JSON ", e);
        } catch (IOException e) {
            logger.error("Failed to connect to VK server ", e);
        }
        return Optional.empty();
    }

    @Override
    public void markAsRead(String userId, String token, String startMessageId) {

        String uriMarkAsRead = vkApi + "messages.markAsRead" +
                "?peer_id=" + userId +
                "&v=" + version +
                "&start_message_id=" + (startMessageId != null && !startMessageId.isEmpty() ? startMessageId : "0") +
                "&access_token=" + token;
        HttpGet httpMarkMessages = new HttpGet(uriMarkAsRead);
        try {
            HttpClient httpClient = getHttpClient();
            HttpResponse response = httpClient.execute(httpMarkMessages);
            logger.info("Message successfully marked as read");
        } catch (IOException e) {
            logger.error("Failed to mark as read message from community", e);
        }
    }

    @Override
    public Optional<Client> getClientFromVkId(Long id) {
        logger.info("VKService: getting client by VK id...");

        //сначала ищем у себя в базе
        Optional<SocialProfile> socialProfileOpt = socialProfileService.getSocialProfileBySocialIdAndSocialType(String.valueOf(id), "vk");
        if (socialProfileOpt.isPresent()) {
            Optional<Client> client = clientService.getClientBySocialProfile(socialProfileOpt.get());
            if (client.isPresent()) {
                return client;
            }
        }
        Map<String, String> param = getUserDataById(id, "", "");

        String name = param.get("first_name");
        String lastName = param.get("last_name");

        try {
            if (name != null && lastName != null) {
                Client.Builder newClientBuilder = new Client.Builder(name, null, null);
                Client newClient = newClientBuilder.lastName(lastName).build();
                SocialProfile socialProfile = new SocialProfile(String.valueOf(id));
                List<SocialProfile> socialProfiles = new ArrayList<>();
                socialProfiles.add(socialProfile);
                newClient.setSocialProfiles(socialProfiles);
                return Optional.of(newClient);
            }
        } catch (NullPointerException e) {
            logger.error("Set social profile error", e);
        }

        return Optional.empty();
    }

    @Override
    public Map<String, String> getUserDataById(Long id, String additionalFields, String splitter) {
        logger.info("VKService: getting user data by VK id...");

        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("id", Long.toString(id));

        String uriGetClient = vkApi + "users.get" +
                "?v=" + version +
                "&user_id=" + id +
                "&fields=" + additionalFields +
                "&access_token=" + communityToken;

        HttpGet httpGetClient = new HttpGet(uriGetClient);
        HttpClient httpClient = getHttpClient();
        try {
            HttpResponse response = httpClient.execute(httpGetClient);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONArray jsonUsers = json.getJSONArray("response");
            JSONObject jsonUser = jsonUsers.getJSONObject(0);

            //имя и фамилия присутствуют всегда
            returnMap.put("first_name", jsonUser.getString("first_name"));
            returnMap.put("last_name", jsonUser.getString("last_name"));

            //можно получить дополнительные поля
            if (additionalFields != null && !additionalFields.isEmpty()) {
                String[] additional = additionalFields.split(splitter);

                for (String param : additional) {
                    returnMap.put(param, jsonUser.getString(param));
                }
            }

        } catch (JSONException e) {
            logger.error("Can not read message from JSON ", e);
        } catch (IOException e) {
            logger.error("Failed to connect to VK server ", e);
        }

        return returnMap;
    }

    @Override
    public Map<String, String> getGroupDataById(Long id, String additionalFields, String splitter) {
        logger.info("VKService: getting group data by VK id...");

        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("id", Long.toString(id));

        String uriGetClient = vkApi + "groups.getById" +
                "?v=" + version +
                "&groupID=" + id +
                "&fields=" + additionalFields +
                "&access_token=" + communityToken;

        HttpGet httpGetClient = new HttpGet(uriGetClient);
        HttpClient httpClient = getHttpClient();
        try {
            HttpResponse response = httpClient.execute(httpGetClient);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONArray jsonUsers = json.getJSONArray("response");
            JSONObject jsonUser = jsonUsers.getJSONObject(0);

            String[] additional = additionalFields.split(splitter);

            for (String param : additional) {
                returnMap.put(param, jsonUser.getString(param));
            }

        } catch (JSONException e) {
            logger.error("Can not read message from JSON ", e);
        } catch (IOException e) {
            logger.error("Failed to connect to VK server ", e);
        }

        return returnMap;
    }

    @Override
    public Optional<Client> getVkLinkById(String userID) {
        Optional<SocialProfile> socialProfile = socialProfileService.getSocialProfileBySocialIdAndSocialType(userID, "vk");
        if (socialProfile.isPresent()) {
            return clientService.getClientBySocialProfile(socialProfile.get());
        }
        return Optional.empty();
    }

    @Override
    public Client parseClientFromMessage(String message) throws ParseClientException {
        logger.info("VKService: parsing client from VK message...");
        if (!message.startsWith("Новая заявка")) {
            throw new ParseClientException("Invalid message format");
        }
        String[] fields = message.replaceAll("<br>", "").split("Q:");
        Client newClient = new Client.Builder(null, null, null).build();
        try {
            StringBuilder description = new StringBuilder();
            int numberVkPosition = 0; // позиция поля в заявке
            int numberMissedPosition = 0;
            List<VkRequestForm> vkRequestForms = vkRequestFormService.getAllVkRequestForm();
            vkRequestForms.sort(Comparator.comparingInt(VkRequestForm::getNumberVkField));
            for (VkRequestForm vkRequestForm : vkRequestForms) {
                numberVkPosition = vkRequestForm.getNumberVkField() - numberMissedPosition;
                if (numberVkPosition > fields.length - 1) {
                    break;
                }
                if ("Поле сопоставленное с данными".equals(vkRequestForm.getTypeVkField())) {
                    switch (vkRequestForm.getNameVkField()) {
                        case "Имя":
                            newClient.setName(getValue(fields[numberVkPosition]));
                            break;
                        case "Фамилия":
                            newClient.setLastName(getValue(fields[numberVkPosition]));
                            break;
                        case "Email":
                            newClient.setEmail(getValue(fields[numberVkPosition]));
                            break;
                        case "Номер телефона":
                            newClient.setPhoneNumber(getValue(fields[numberVkPosition]));
                            break;
                        case "Skype":
                            newClient.setSkype(getValue(fields[numberVkPosition]));
                            break;
                        case "Город":
                            newClient.setCity(getValue(fields[numberVkPosition]));
                            break;
                        case "Страна":
                            newClient.setCountry(getValue(fields[numberVkPosition]));
                            break;
                        case "Пол":
                            String sexStringValue = getValue(fields[numberVkPosition]).replaceAll("\\s+|\\d", "");
                            if (sexStringValue.equals("Мужской") || sexStringValue.equals("Мужчина")) {
                                newClient.setSex(Sex.MALE);
                            } else if (sexStringValue.equals("Женский") || sexStringValue.equals("Женщина")) {
                                newClient.setSex(Sex.FEMALE);
                            } else {
                                logger.info("В поле \"Пол\" в форме заявки ВК на выбор клиенту должен придоставляться выбор либо \"Мужской либо Женский\" либо \"Мужчина либо Женщина\"");
                            }
                            break;
                    }
                } else {
                    if (message.contains(vkRequestForm.getNameVkField())) {
                        description.append(vkRequestForm.getNameVkField()).append(": ").append(getValue(fields[numberVkPosition])).append(" \n");
                    } else {
                        numberMissedPosition++;
                    }
                }
            }

            newClient.setClientDescriptionComment(description.toString());
            String social = fields[0];
            Optional<String> socialId = getIdFromLink("https://" + social.substring(social.indexOf("vk.com/id"), social.indexOf("Диалог")));
            if (socialId.isPresent()) {
                SocialProfile socialProfile = new SocialProfile(socialId.get(), SocialNetworkType.VK);
                newClient.setSocialProfiles(Collections.singletonList(socialProfile));
            }
        } catch (Exception e) {
            logger.error("Parse error, can't parse income string", e);
        }
        return newClient;
    }

    private static String getValue(String field) {
        return field.substring(field.indexOf("A: ") + 3);
    }

    @Override
    public String refactorAndValidateVkLink(String link) {
        logger.info("VKService: refactoring and validation of VK link...");
        String userName = link.replaceAll("^.+\\.(com/)", "");
        String request = vkApi + "users.get?"
                + "user_ids=" + userName
                + "&fields=first_name"
                + "&access_token=" + communityToken
                + "&v=" + version;
        HttpGet httpGetClient = new HttpGet(request);
        HttpClient httpClient = getHttpClient();
        try {
            HttpResponse response = httpClient.execute(httpGetClient);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONArray responseArray = json.getJSONArray("response");
            JSONObject vkUserJson = responseArray.getJSONObject(0);
            String vkId = vkUserJson.getString("id");
            if (vkUserJson.has("deactivated")) {
                logger.error("VkUser with id {} don't validate", vkId);
                return "undefined";
            }
            return "https://vk.com/id" + vkId;
        } catch (JSONException e) {
            logger.error("Can't take id by screen name {}", userName);
            return "undefined";
        } catch (IOException e) {
            logger.error("Failed to connect to VK server ", e);
            return link;
        }
    }

    private String replaceName(String msg, Map<String, String> params) {
        String vkText = msg;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            vkText = String.valueOf(new StringBuilder(vkText.replaceAll(entry.getKey(), entry.getValue())));
        }
        return vkText;
    }

    @Override
    public void setTechnicalAccountToken(String technicalAccountToken) {
        this.technicalAccountToken = technicalAccountToken;
    }

    @Override
    public String replaceApplicationTokenFromUri(String uri) {
        return uri.replaceAll(".+(access_token=)", "")
                .replaceAll("&.+", "");
    }

    @Override
    public String createNewAudience(String groupName, String idVkCabinet) throws Exception {
        logger.info("VKService: creation of new audience...");
        String createGroup = "https://api.vk.com/method/ads.createTargetGroup";
        OAuth2AccessToken accessToken = new OAuth2AccessToken(technicalAccountToken);
        OAuthRequest request = new OAuthRequest(Verb.GET, createGroup);
        request.addParameter("account_id", idVkCabinet);
        request.addParameter("name", groupName);
        request.addParameter("v", version);
        service.signRequest(accessToken, request);
        Response response = service.execute(request);
        String resp = new JSONObject(response.getBody()).get("response").toString();
        String groupId = new JSONObject(resp).get("id").toString();
        return groupId;
    }

    @Override
    public void addUsersToAudience(String groupId, String contacts, String idVkCabinet) throws Exception {
        logger.info("VKService: adding users to audience...");
        String addContactsToGroup = "https://api.vk.com/method/ads.importTargetContacts";
        OAuth2AccessToken accessToken = new OAuth2AccessToken(technicalAccountToken);
        OAuthRequest request = new OAuthRequest(Verb.POST, addContactsToGroup);
        request.addParameter("account_id", idVkCabinet);
        request.addParameter("target_group_id", groupId);
        request.addParameter("contacts", contacts);
        request.addParameter("v", version);
        service.signRequest(accessToken, request);
        Response response = service.execute(request);
    }

    @Override
    public void removeUsersFromAudience(String groupId, String contacts, String idVkCabinet) throws Exception {
        logger.info("VKService: removing users to audience...");
        String addContactsToGroup = "https://api.vk.com/method/ads.removeTargetContacts";
        OAuth2AccessToken accessToken = new OAuth2AccessToken(technicalAccountToken);
        OAuthRequest request = new OAuthRequest(Verb.POST, addContactsToGroup);
        request.addParameter("account_id", idVkCabinet);
        request.addParameter("target_group_id", groupId);
        request.addParameter("contacts", contacts);
        request.addParameter("v", version);
        service.signRequest(accessToken, request);
        Response response = service.execute(request);
    }


    @Override
    public String getFirstContactMessage() {
        return firstContactMessage;
    }

    @Override
    public boolean hasTechnicalAccountToken() {
        if (technicalAccountToken == null) {
            //токен берется из первой строки таблицы project_properties (при этом сохраняется последняя строка авторизованного в вк пользователя срм)
            //можно изменить функции где он исползуется и убрать его из проекта за ненадобностью, так как похож на костыль.
            technicalAccountToken = projectPropertiesService.getOrCreate().getTechnicalAccountToken();
            if (technicalAccountToken == null) {
                logger.info("VK access token has not got");
                return false;
            }
        }
        return true;
    }

    @Override
    public String getLongIDFromShortName(String vkGroupShortName) {
        if (hasTechnicalAccountToken()) {
            String uriGetGroup = vkApi + "groups.getById?" +
                    "group_id=" + vkGroupShortName +
                    "&v=" + version +
                    "&access_token=" + technicalAccountToken;
            HttpGet httpGetGroup = new HttpGet(uriGetGroup);
            HttpClient httpClient = getHttpClient();
            try {
                HttpResponse response = httpClient.execute(httpGetGroup);
                String result = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(result);
                JSONArray responseObjects = json.getJSONArray("response");
                JSONObject responseObject = responseObjects.getJSONObject(0);
                String id = responseObject.getString("id");
                return id;
            } catch (JSONException e) {
                logger.error("Can not read message from JSON", e);
            } catch (IOException e) {
                logger.error("Failed to connect to VK server ", e);
            }
        } else {
            logger.error("VK access token has not got");
        }
        return null;
    }

    @Override
    public Optional<PotentialClient> getPotentialClientFromYoutubeLiveStreamByYoutubeClient(YoutubeClient youtubeClient) {
        if (hasTechnicalAccountToken()) {
            youtubeClient.setChecked(true);
            youtubeClientService.update(youtubeClient);
            String fullName = youtubeClient.getFullName().replaceAll("(?U)[\\pP\\s]", "%20");
            logger.info("VKService: getting client from YouTube Live Stream by name: " + fullName);
            String uriGetClient = vkApi + "users.search?" +
                    "q=" + fullName +
                    "&count=1" +
                    "&group_id=" + youtubeClient.getYouTubeTrackingCard().getVkGroupID() +
                    "&v=" + version +
                    "&access_token=" + technicalAccountToken;

            HttpGet httpGetClient = new HttpGet(uriGetClient);
            HttpClient httpClient = getHttpClient();
            try {
                HttpResponse response = httpClient.execute(httpGetClient);
                String result = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(result);
                JSONObject responseObject = json.getJSONObject("response");

                if (responseObject.getString("count").equals("0")) {
                    logger.warn("VKService: response is empty");
                    return Optional.empty();
                } else {
                    logger.info("VKService: processing of response...");
                    JSONArray jsonUsers = responseObject.getJSONArray("items");
                    JSONObject jsonUser = jsonUsers.getJSONObject(0);
                    long id = jsonUser.getLong("id");
                    String firstName = jsonUser.getString("first_name");
                    String lastName = jsonUser.getString("last_name");
                    String vkLink = "https://vk.com/id" + id;
                    PotentialClient potentialClient = new PotentialClient(firstName, lastName);
                    SocialProfile socialProfile = new SocialProfile(vkLink);
                    socialProfile.setSocialNetworkType(SocialNetworkType.VK);
                    List<SocialProfile> socialProfiles = new ArrayList<>();
                    socialProfiles.add(socialProfile);
                    potentialClient.setSocialProfiles(socialProfiles);
                    return Optional.of(potentialClient);
                }
            } catch (JSONException e) {
                logger.error("Can not read message from JSON or YoutubeClient don't exist in VK group", e);
            } catch (IOException e) {
                logger.error("Failed to connect to VK server ", e);
            }
        } else {
            logger.error("VK access token has not got");
        }
        return Optional.empty();
    }

    @Override
    public String getVkPhotoLinkByClientProfileId(String vkProfileId) {
        logger.info("Getting vk profile photo link for " + vkProfileId);


        String clientVkPhotoLink = "";

        String clientId = (Pattern.matches("^https://vk.com/id\\d{1,10}$", vkProfileId)) ?
                vkProfileId.replaceAll("https://vk.com/id", "") :
                vkProfileId.replaceAll("https://vk.com/", "");

        String request = vkApi + "users.get?"
                + "user_ids=" + clientId
                + "&fields=photo_50"
                + "&access_token=" + communityToken
                + "&v=" + version;

        HttpGet httpGetClient = new HttpGet(request);
        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        try {
            HttpResponse response = httpClient.execute(httpGetClient);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONArray array = json.getJSONArray("response");
            JSONObject user = array.getJSONObject(0);
            clientVkPhotoLink = user.getString("photo_50");
        } catch (JSONException jex) {
            logger.error("Failed to parse response into JSON for "
                    + clientId + "- for reason " + jex);
        } catch (IOException ex) {
            logger.error("Failed to connect to VK server while getting profile pic for "
                    + clientId + "- for reason " + ex);
        }
        return clientVkPhotoLink;
    }

    @Override
    public Optional<String> getIdFromLink(String link) {

        Pattern p = Pattern.compile(vkPattern, Pattern.CASE_INSENSITIVE);

        Matcher vkMatcher = p.matcher(link);
        if (vkMatcher.find() && !link.contains(" ") && !link.equals(Strings.EMPTY)) {

            String vkIdentify = vkMatcher.group();
            if (vkIdentify.startsWith(idString) && vkIdentify.replace(idString, Strings.EMPTY).matches(allDigitPattern)) {
                vkIdentify = vkIdentify.replace(idString, Strings.EMPTY);
            }

            if (!vkIdentify.matches(allDigitPattern)) {
                vkIdentify = Long.toString(getVKIdByUrl(vkURL + vkIdentify).orElse(0L));
            }

            if (!zeroString.equals(vkIdentify)) {
                return Optional.ofNullable(vkIdentify);
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("deprecation")
    private static String getByteArrayFromImageURL(String url) {
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();
            InputStream is = ucon.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            return new String(Base64.encode(baos.toByteArray()));
        } catch (Exception e) {
            logger.error("Failed to get array from image url " + url, e);
        }
        return null;
    }

    private String getResultCaptcha(String taskId) throws JSONException, IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject bodyForResult = new JSONObject("{\n" +
                "    \"clientKey\":\"" + userKey + "\",\n" +
                "    \"taskId\": " + taskId + " \n" +
                "}");

        RequestBody bodyResult = RequestBody.create(JSON, bodyForResult.toString());
        Request requestResult = new Request.Builder()
                .url("https://api.anti-captcha.com/getTaskResult").
                        post(bodyResult).build();
        com.squareup.okhttp.Response responseResult = client.newCall(requestResult).execute();
        JsonObject convertedObjectResult = new Gson().fromJson(responseResult.body().string(), JsonObject.class);
        return convertedObjectResult.getAsJsonObject("solution").get("text").getAsString();
    }

    @Override
    public Optional<VkProfileInfo> getProfileInfoById(long vkId) {
        VkProfileInfo vkProfileInfo = new VkProfileInfo();
        vkProfileInfo.setVkId(vkId);
        String fileds = "first_name,last_name,sex,bdate,country,city,education,has_mobile,contacts";
        String request = vkApi + "users.get?" +
                "user_ids=" + vkId +
                "&fields=" + fileds +
                "&access_token=" + communityToken +
                "&v=" + version;
        HttpGet httpGetClient = new HttpGet(request);
        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build()).build();
        try {
            HttpResponse response = httpClient.execute(httpGetClient);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONArray responseArray = json.getJSONArray("response");
            JSONObject vkUserJson = responseArray.getJSONObject(0);
            if (vkUserJson.has("first_name")) {
                vkProfileInfo.setFirstName(vkUserJson.getString("first_name"));
            }
            if (vkUserJson.has("last_name")) {
                vkProfileInfo.setLastName(vkUserJson.getString("last_name"));
            }
            if (vkUserJson.has("country")) {
                JSONObject jsonEdv = new JSONObject(vkUserJson.getString("country"));
                if (jsonEdv.has("title")) {
                    vkProfileInfo.setCountry(jsonEdv.getString("title"));
                }
            }
            if (vkUserJson.has("city")) {
                JSONObject jsonEdv = new JSONObject(vkUserJson.getString("city"));
                if (jsonEdv.has("title")) {
                    vkProfileInfo.setCity(jsonEdv.getString("title"));
                }
            }
            if (vkUserJson.has("sex")) {
                if ("2".equals(vkUserJson.getString("sex"))) {
                    vkProfileInfo.setSex(Sex.MALE);
                } else if ("1".equals(vkUserJson.getString("sex"))) {
                    vkProfileInfo.setSex(Sex.FEMALE);
                }
            }
            if (vkUserJson.has("bdate")) {
                String birthDate = vkUserJson.getString("bdate");
                Pattern pattern = Pattern.compile("\\d{1,2}\\.\\d{1,2}.\\d{4}");
                Matcher matcher = pattern.matcher(birthDate);
                if (matcher.find()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
                    vkProfileInfo.setBirthdate(LocalDate.parse(birthDate, formatter));
                }
            }
            if (vkUserJson.has("university_name")) {
                vkProfileInfo.setUniversity(vkUserJson.getString("university_name"));
            }
            if (vkUserJson.has("mobile_phone")) {
                vkProfileInfo.setPhone(phoneValidator.phoneRestore(vkUserJson.getString("mobile_phone")));
            }
            return Optional.of(vkProfileInfo);
        } catch (IOException e) {
            logger.error("Failed to connect to VK server", e);
        } catch (JSONException e) {
            logger.error("Can't take info from vk profile by vk id = {}", vkId, e);
        }

        return Optional.empty();
    }

    @Override
    public void fillClientFromProfileVK(Client client) {
        Optional<VkProfileInfo> vkProfileInfo = Optional.empty();

        Optional<SocialProfile> socialProfile = Optional.empty();
        for (SocialProfile socialProfileElement : client.getSocialProfiles()) {
            if ("vk".equals(socialProfileElement.getSocialNetworkType().getName())) {
                socialProfile = Optional.of(socialProfileElement);
                break;
            }
        }
        if (socialProfile.isPresent()) {
            long vkId = getVKIdByUrl("https://vk.com/" + socialProfile.get().getSocialId()).orElse(0L);
            if (vkId > 0) {
                vkProfileInfo = getProfileInfoById(vkId);
            }
        }
        if (vkProfileInfo.isPresent()) {
            VkProfileInfo vkInfo = vkProfileInfo.get();

            if (client.getCity() == null || client.getCity().isEmpty()) {
                client.setCity(vkInfo.getCity());
            }
            if (client.getBirthDate() == null) {
                client.setBirthDate(vkInfo.getBirthdate());
            }
            if (client.getSex() == null) {
                client.setSex(vkInfo.getSex());
            }
            if (!client.getPhoneNumber().isPresent() || client.getPhoneNumber().get().isEmpty()) {
                client.setPhoneNumber(vkInfo.getPhone());
            }
            if (client.getCountry() == null || client.getCountry().isEmpty()) {
                client.setCountry(vkInfo.getCountry());
            }
            if (client.getUniversity() == null || client.getUniversity().isEmpty()) {
                client.setUniversity(vkInfo.getUniversity());
            }
        }
    }

    @Override
    public void sendDailyAdvertisementReport(String template) {
        logger.debug("Start filling ad report template");
        String balanceFromYandexDirect;
        String spentFromYandexDirect;
        String balanceFromVk;
        String spentFromVk;
        String balanceFromGoogle;
        String spentFromGoogle;
        String GETTING_REPORT_ERROR = env.getProperty("messaging.vk.get-report-error");

        // Получение отчёта с Yandex Direct
        try {
            balanceFromYandexDirect = yandexDirectAdReportService.getBalance();
        } catch (Exception e) {
            logger.error("Can't receive balance from Yandex Direct. Trying again", e);
            try {
                balanceFromYandexDirect = yandexDirectAdReportService.getBalance();
            } catch (Exception e1) {
                balanceFromYandexDirect = GETTING_REPORT_ERROR;
                logger.error("Can't receive balance from Yandex Direct. Check if request to YaD API, service response or response parsing are correct", e1);
            }
        }
        try {
            spentFromYandexDirect = yandexDirectAdReportService.getSpentMoney();
        } catch (Exception e) {
            logger.error("Can't receive campaign report from Yandex Direct. Trying again", e);
            try {
                spentFromYandexDirect = yandexDirectAdReportService.getSpentMoney();
            } catch (Exception e1) {
                spentFromYandexDirect = GETTING_REPORT_ERROR;
                logger.error("Can't receive campaign report from Yandex Direct. Check if request to YaD API, service response or response parsing are correct", e1);
            }
        }
        // Получение отчёта со ВКонтакте.
        try {
            balanceFromVk = vkAdsReportService.getBalance();
        } catch (Exception e) {
            balanceFromVk = GETTING_REPORT_ERROR;
            logger.error("Can't receive balance from VK. Check if request to VK ads API is correct", e);
        }
        try {
            spentFromVk = vkAdsReportService.getSpentMoney();
        } catch (Exception e) {
            spentFromVk = GETTING_REPORT_ERROR;
            logger.error("Can't receive spent money report from VK ads. Check if request to VK ads API is correct", e);
        }

        //Получение отчета Google Ads
        try {
            balanceFromGoogle = googleAdsService.getAccountBalance();
        } catch (RemoteException e) {
            balanceFromGoogle = GETTING_REPORT_ERROR;
            logger.error("Can't get BOS from Google ads API", e);
        } catch (ReportException | ReportDownloadResponseException e) {
            balanceFromGoogle = GETTING_REPORT_ERROR;
            logger.error("Can't get report from Google ads API", e);
        } catch (IOException e) {
            balanceFromGoogle = GETTING_REPORT_ERROR;
            logger.error("Can't download report from Google ads API", e);
        } catch (NumberFormatException e) {
            balanceFromGoogle = GETTING_REPORT_ERROR;
            logger.error("Can't parse amount value from Google ads API report", e);
        } catch (Exception e) {
            balanceFromGoogle = GETTING_REPORT_ERROR;
            logger.error("Unknown exception in Google ads API report", e);
        }
        try {
            spentFromGoogle = googleAdsService.getYesterdaySpentAmount();
        } catch (ReportException | ReportDownloadResponseException e) {
            spentFromGoogle = GETTING_REPORT_ERROR;
            logger.error("Can't get report from Google ads API", e);
        } catch (IOException e) {
            spentFromGoogle = GETTING_REPORT_ERROR;
            logger.error("Can't download report from Google ads API", e);
        } catch (NumberFormatException e) {
            spentFromGoogle = GETTING_REPORT_ERROR;
            logger.error("Can't parse amount value from Google ads API report", e);
        } catch (Exception e) {
            spentFromGoogle = GETTING_REPORT_ERROR;
            logger.error("Unknown exception in Google ads API report", e);
        }

        //Формирование окончательного вида сообщения, заполнение параметров шаблона
        Object[] params = {balanceFromYandexDirect, spentFromYandexDirect,
                balanceFromVk, spentFromVk,
                balanceFromGoogle, spentFromGoogle};
        String message = MessageFormat.format(template, params);

        logger.debug("End filling ad report template");
        logger.debug("Start sending ad report to VK: {}" + message);
        //Отправка в диалог
        sendMessageByChatId(vkReportChatId, message);
    }

    @Override
    public Optional<String> getAllCountries() {
        StringBuilder uri = new StringBuilder(vkApi).append("database.getCountries")
                .append("?access_token=").append(vkAppAccessToken)
                .append("&v=").append(version)
                .append("&need_all=1")
                .append("&count=1000");
        HttpGet httpGetStat = new HttpGet(uri.toString());
        HttpClient httpClientStat = getHttpClient();
        HttpResponse response;
        String result = null;
        try {
            response = httpClientStat.execute(httpGetStat);
            result = EntityUtils.toString(response.getEntity()).replace("title", "value");
        } catch (IOException e) {
            logger.error("Can't get all countries list from VK api", e);
        }
        return Optional.of(result);
    }


    @Override
    public Optional<String> getCitiesByCountry(int countryId, String query) {
        StringBuilder uri = new StringBuilder(vkApi).append("database.getCities")
                .append("?access_token=").append(vkAppAccessToken)
                .append("&v=").append(version)
                .append("&country_id=").append(countryId)
                .append("&q=").append(query)
                .append("&need_all=1")
                .append("&count=1000");
        HttpGet httpGetStat = new HttpGet(uri.toString());
        HttpClient httpClientStat = getHttpClient();
        HttpResponse response;
        String result = null;
        try {
            response = httpClientStat.execute(httpGetStat);
            result = EntityUtils.toString(response.getEntity()).replace("title", "value");
        } catch (IOException e) {
            logger.error("Can't get all cities list by country id from VK api", e);
        }
        return Optional.of(result);
    }
}