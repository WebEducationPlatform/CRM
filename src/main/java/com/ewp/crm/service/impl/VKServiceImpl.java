package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.exceptions.parse.ParseClientException;
import com.ewp.crm.exceptions.util.VKAccessTokenException;
import com.ewp.crm.models.*;
import com.ewp.crm.models.Client.Sex;
import com.ewp.crm.service.interfaces.*;
import com.github.scribejava.apis.VkontakteApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
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
    private final SocialProfileTypeService socialProfileTypeService;
    private final UserService userService;
    private final ProjectPropertiesService projectPropertiesService;
    private final VkRequestFormService vkRequestFormService;
    private final VkMemberService vkMemberService;

    private String vkAPI;
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

    @Value("${userKey}")
    private String userKey;

    @Autowired
    public VKServiceImpl(VKConfig vkConfig,
                         YoutubeClientService youtubeClientService,
                         SocialProfileService socialProfileService,
                         ClientHistoryService clientHistoryService,
                         ClientService clientService,
                         MessageService messageService,
                         SocialProfileTypeService socialProfileTypeService,
                         UserService userService,
                         MessageTemplateService messageTemplateService,
                         ProjectPropertiesService projectPropertiesService,
                         VkRequestFormService vkRequestFormService,
                         VkMemberService vkMemberService) {
        clubId = vkConfig.getClubIdWithMinus();
        version = vkConfig.getVersion();
        communityToken = vkConfig.getCommunityToken();
        applicationId = vkConfig.getApplicationId();
        display = vkConfig.getDisplay();
        redirectUri = vkConfig.getRedirectUri();
        scope = vkConfig.getScope();
        vkAPI = vkConfig.getVkAPIUrl();
        managerToken = vkConfig.getManagerToken();
        this.youtubeClientService = youtubeClientService;
        this.socialProfileService = socialProfileService;
        this.clientHistoryService = clientHistoryService;
        this.clientService = clientService;
        this.messageService = messageService;
        this.socialProfileTypeService = socialProfileTypeService;
        this.userService = userService;
        this.projectPropertiesService = projectPropertiesService;
        this.vkRequestFormService = vkRequestFormService;
        this.vkMemberService = vkMemberService;
        this.service = new ServiceBuilder(clubId).build(VkontakteApi.instance());
        this.firstContactMessage = vkConfig.getFirstContactMessage();
    }

    @Override
    public String receivingTokenUri() {

        return "https://oauth.vk.com/authorize" +
                "?client_id=" + applicationId +
                "&display=" + display +
                "&redirect_uri=" + redirectUri +
                "&scope=" + scope +
                "&response_type=token" +
                "&v" + version;
    }

    @Override
    public Optional<List<String>> getNewMassages() throws VKAccessTokenException {
        logger.info("VKService: getting new messages...");
        if (technicalAccountToken == null && (technicalAccountToken = projectPropertiesService.get() != null ? projectPropertiesService.get().getTechnicalAccountToken() : null) == null) {
            throw new VKAccessTokenException("VK access token has not got");
        }
        String uriGetMassages = vkAPI + "messages.getHistory" +
                "?user_id=" + clubId +
                "&rev=0" +
                "&version=" + version +
                "&access_token=" + technicalAccountToken;
        try {
            HttpGet httpGetMessages = new HttpGet(uriGetMassages);
            HttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            HttpResponse response = httpClient.execute(httpGetMessages);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONArray jsonMessages = json.getJSONArray("response");
            List<String> resultList = new ArrayList<>();
            for (int i = 1; i < jsonMessages.length(); i++) {
                JSONObject jsonMessage = jsonMessages.getJSONObject(i);
                if ((clubId.equals(jsonMessage.getString("uid"))) && (jsonMessage.getInt("read_state") == 0)) {

                    String messageBody = jsonMessage.getString("body");
                    resultList.add(messageBody);

                    if (messageBody.startsWith("Новая заявка")) {
                        markAsRead(Long.parseLong(clubId), httpClient, technicalAccountToken);
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
    public void sendMessageToClient(Long clientId, String templateText, String body, User principal) {
        Client client = clientService.getClientByID(clientId);
        String fullName = client.getName() + " " + client.getLastName();
        Map<String, String> params = new HashMap<>();
        params.put("%fullName%", fullName);
        params.put("%bodyText%", body);
        params.put("%dateOfSkypeCall%", body);
        List<SocialProfile> socialProfiles = client.getSocialProfiles();
        for (SocialProfile socialProfile : socialProfiles) {
            if (socialProfile.getSocialProfileType().getName().equals("vk")) {
                String link = socialProfile.getLink();
                Optional<Long> optId = getVKIdByUrl(link);
                if (optId.isPresent()) {
                    Long id = optId.get();
                    String vkText = replaceName(templateText, params);
                    String token = communityToken;
                    if (principal != null) {
                        User user = userService.get(principal.getId());
                        if (user.getVkToken() != null) {
                            token = user.getVkToken();
                        }
                        Message message = messageService.addMessage(Message.Type.VK, vkText);
                        client.addHistory(clientHistoryService.createHistory(principal, client, message));
                        clientService.updateClient(client);
                    }
                    sendMessageById(id, vkText, token);
                } else {
                    logger.info("{} has wrong VK url {}", client.getEmail(), link);
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
        Optional<Long> result = Optional.empty();
        if (url.matches("(.*)://vk.com/id(\\d*)")) {
            result = Optional.of(Long.parseLong(url.replaceAll(".+id", "")));
        } else if (url.matches("(.*)://vk.com/(.*)")) {
            String screenName = url.substring(url.lastIndexOf("/") + 1);
            String urlGetMessages = vkAPI + "users.get" +
                    "?user_ids=" + screenName +
                    "&version=" + version +
                    "&access_token=" + communityToken;
            try {
                HttpGet httpGetMessages = new HttpGet(urlGetMessages);
                HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                        .build();
                HttpResponse httpResponse = httpClient.execute(httpGetMessages);
                String entity = EntityUtils.toString(httpResponse.getEntity());
                JSONArray users = new JSONObject(entity).getJSONArray("response");
                result = Optional.of(users.getJSONObject(0).getLong("uid"));
            } catch (IOException e) {
                logger.error("Failed to connect to VK server", e);
            } catch (JSONException e) {
                logger.error("Can not read message from JSON", e);
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
        String urlGetMessages = vkAPI + "groups.getMembers" +
                "?group_id=" + groupId +
                "&offset=" + offset +
                "&version=" + version +
                "&access_token=" + communityToken;
        try {
            HttpGet httpGetMessages = new HttpGet(urlGetMessages);
            HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
            HttpResponse httpResponse = httpClient.execute(httpGetMessages);
            String result = EntityUtils.toString(httpResponse.getEntity());
            JSONObject json = new JSONObject(result);
            JSONObject responeJson = json.getJSONObject("response");
            JSONArray jsonArray = responeJson.getJSONArray("users");
            List<VkMember> vkMembers = new LinkedList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                VkMember vkMember = new VkMember(Long.parseLong(jsonArray.get(i).toString()), groupId);
                if (vkMemberService.getVkMemberById(vkMember.getVkId()) == null) {
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
        return sendMessageById(id, msg, managerToken);
    }


    @Override
    public String sendMessageById(Long id, String msg, String token) {

        logger.info("VKService: sending message to client with id {}...", id);

        String replaceCarriage = msg.replaceAll("(\r\n|\n)", "%0A")
                .replaceAll("\"|\'", "%22");
        String uriMsg = replaceCarriage.replaceAll("\\s", "%20");

        String sendMsgRequest = vkAPI + "messages.send" +
                "?user_id=" + id +
                "&v=" + version +
                "&message=" + uriMsg +
                "&access_token=" + token;

        HttpGet request = new HttpGet(sendMsgRequest);
        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        try {
            HttpResponse response = httpClient.execute(request);
            JSONObject jsonEntity = new JSONObject(EntityUtils.toString(response.getEntity()));
            JsonObject convertedJsonEntity = new Gson().fromJson(jsonEntity.toString(), JsonObject.class);

            if (jsonEntity.toString().contains("Permission to perform this action is denied") | jsonEntity.toString().contains("Can't send messages to this user due to their privacy settings")) {
                JSONArray jsonArray = jsonEntity.getJSONObject("error").getJSONArray("request_params");
                for(int i = 0; i < jsonArray.length(); i++) {
                    if(jsonArray.getJSONObject(i).getString("key").equalsIgnoreCase("user_id")) {
                        return jsonArray.getJSONObject(i).getString("value");
                    }
                }
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

                String sendMsgWithCaptcha = vkAPI + "messages.send" +
                            "?user_id=" + id +
                            "&v=" + version +
                            "&message=" + uriMsg +
                            "&access_token=" + token +
                            "&captcha_sid=" + captchaSid +
                            "&captcha_key=" + text;

                HttpGet newRequest = new HttpGet(sendMsgWithCaptcha);
                HttpResponse newResponse = httpClient.execute(newRequest);
                JSONObject newJsonEntity = new JSONObject(EntityUtils.toString(newResponse.getEntity()));
                if (newJsonEntity.toString().contains("Permission to perform this action is denied") | newJsonEntity.toString().contains("Can't send messages to this user due to their privacy settings")) {
                    JSONArray newJsonArray = newJsonEntity.getJSONObject("error").getJSONArray("request_params");
                    for(int i = 0; i < newJsonArray.length(); i++) {
                        if(newJsonArray.getJSONObject(i).getString("key").equalsIgnoreCase("user_id")) {
                            return newJsonArray.getJSONObject(i).getString("value");
                        }
                    }
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
        return "Failed to send message";
    }

    // Determine text, which varies depending of the success of the sending message
    private String determineResponse(JSONObject jsonObject) throws JSONException {
        try {
            jsonObject.getInt("response");
            return "Message sent";
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
        String uriGetDialog = vkAPI + "messages.getDialogs" +
                "?v=" + version +
                "&unread=1" +
                "&access_token=" +
                communityToken;

        HttpGet httpGetDialog = new HttpGet(uriGetDialog);
        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
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

    private void markAsRead(long userId, HttpClient httpClient, String token) {
        String uriMarkAsRead = vkAPI + "messages.markAsRead" +
                "?peer_id=" + userId +
                "&version=" + version +
                "&access_token=" + token;

        HttpGet httpMarkMessages = new HttpGet(uriMarkAsRead);
        try {
            httpClient.execute(httpMarkMessages);
        } catch (IOException e) {
            logger.error("Failed to mark as read message from community", e);
        }
    }

    @Override
    public Optional<Client> getClientFromVkId(Long id) {
        logger.info("VKService: getting client by VK id...");

        //сначала ищем у себя в базе
        String vkLink = "https://vk.com/id" + id;
        SocialProfile socialProfile = socialProfileService.getSocialProfileByLink(vkLink);
        Client client = clientService.getClientBySocialProfile(socialProfile);

        if (client != null) {
            return Optional.of(client);
        }

        //потом ломимся в контакт
        String uriGetClient = vkAPI + "users.get?" +
                "version=" + version +
                "&user_id=" + id +
                "&access_token=" + communityToken;

        HttpGet httpGetClient = new HttpGet(uriGetClient);
        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
        try {
            HttpResponse response = httpClient.execute(httpGetClient);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONArray jsonUsers = json.getJSONArray("response");
            JSONObject jsonUser = jsonUsers.getJSONObject(0);
            String name = jsonUser.getString("first_name");
            String lastName = jsonUser.getString("last_name");

            client = new Client(name, lastName);
            socialProfile = new SocialProfile(vkLink);
            List<SocialProfile> socialProfiles = new ArrayList<>();
            socialProfiles.add(socialProfile);
            client.setSocialProfiles(socialProfiles);
            return Optional.of(client);
        } catch (JSONException e) {
            logger.error("Can not read message from JSON ", e);
        } catch (IOException e) {
            logger.error("Failed to connect to VK server ", e);
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
        Client newClient = new Client();
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
                        case "Возраст":
                            String ageStringValue = getValue(fields[numberVkPosition]).replaceAll("\\D", "");
                            byte age = 0;
                            try {
                                age = Byte.parseByte(ageStringValue);
                            } catch (NumberFormatException e) {
                                logger.info("В заявке формы вк был введено не допустимое значение возраста", e);
                            }
                            newClient.setAge(age);
                            break;
                        case "Город":
                            newClient.setCity(getValue(fields[numberVkPosition]));
                            break;
                        case "Страна":
                            newClient.setCountry(getValue(fields[numberVkPosition]));
                            break;
                        case "Пол":
                            String sexStringValue = getValue(fields[numberVkPosition]).replaceAll("\\s+|\\d", "");
                            if (sexStringValue.equals("Мужской")||sexStringValue.equals("Мужчина")) {
                                newClient.setSex(Sex.MALE);
                            } else if (sexStringValue.equals("Женский")||sexStringValue.equals("Женщина")) {
                                newClient.setSex(Sex.FEMALE);
                            }else {
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
            SocialProfileType socialProfileType = socialProfileTypeService.getByTypeName("vk");
            String social = fields[0];
            SocialProfile socialProfile = new SocialProfile("https://" + social.substring(social.indexOf("vk.com/id"), social.indexOf("Диалог")), socialProfileType);
            newClient.setSocialProfiles(Collections.singletonList(socialProfile));
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
        String request = vkAPI + "users.get?"
                + "user_ids=" + userName
                + "&fields=first_name"
                + "&access_token=" + communityToken
                + "&v=" + version;
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
            String uriGetGroup = vkAPI + "groups.getById?" +
                    "group_id=" + vkGroupShortName +
                    "&v=" + version +
                    "&access_token=" + technicalAccountToken;
            HttpGet httpGetGroup = new HttpGet(uriGetGroup);
            HttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
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
            String uriGetClient = vkAPI + "users.search?" +
                    "q=" + fullName +
                    "&count=1" +
                    "&group_id=" + youtubeClient.getYouTubeTrackingCard().getVkGroupID() +
                    "&v=" + version +
                    "&access_token=" + technicalAccountToken;

            HttpGet httpGetClient = new HttpGet(uriGetClient);
            HttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
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
                    socialProfile.setSocialProfileType(socialProfileTypeService.getByTypeName("vk"));
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

        String request = vkAPI + "users.get?"
                + "user_ids=" + clientId
                + "&fields=photo_50"
                + "&access_token=" + communityToken
                + "&v=" + version;

        HttpGet httpGetClient = new HttpGet(request);
        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        try{
            HttpResponse response = httpClient.execute(httpGetClient);
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(result);
            JSONArray array = json.getJSONArray("response");
            JSONObject user = array.getJSONObject(0);
            clientVkPhotoLink = user.getString("photo_50");
        }catch (JSONException jex){
            logger.error("Failed to parse response into JSON for "
                    + clientId + "- for reason " + jex);
        }
        catch (IOException ex){
            logger.error("Failed to connect to VK server while getting profile pic for "
                    + clientId + "- for reason " + ex);
        }
        return clientVkPhotoLink;
    }

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
            return Base64.encode(baos.toByteArray());
        } catch (Exception e) {
            System.out.println("error");
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
}

