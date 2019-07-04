package com.ewp.crm.service.slack;

import com.ewp.crm.models.*;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.repository.interfaces.StudentRepository;
import com.ewp.crm.service.interfaces.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@PropertySources(value = {
        @PropertySource("classpath:application.properties"),
//        @PropertySource("file:./slack.properties"),
        @PropertySource("file:./slackbot.properties")
})
public class SlackServiceImpl implements SlackService {
    private static final String SLACK_API_URL = "https://slack.com/api/";
    private static final String URL_ENCODING_CHARSET = "UTF-8";
    private static Logger logger = LoggerFactory.getLogger(SlackServiceImpl.class);
    private final StudentService studentService;
    private final ProjectProperties projectProperties;
    private final SocialProfileService socialProfileService;
    private final ClientService clientService;
    private final String slackWorkspaceUrl;
    //    private String appToken;
//    private final String legacyToken;
    private final String generalChannelId;
    private final String defaultPrivateGroupNameTemplate;
    private final AssignSkypeCallService assignSkypeCallService;
    private final MessageTemplateService messageTemplateService;
    private final StudentRepository studentRepository;
    private final String slackBotUrl;

    @Autowired
    public SlackServiceImpl(Environment environment, StudentService studentService,
                            ClientService clientService,
                            SocialProfileService socialProfileService,
                            ProjectPropertiesService projectPropertiesService,
                            AssignSkypeCallService assignSkypeCallService,
                            MessageTemplateService messageTemplateService,
                            StudentRepository studentRepository) {
//        this.appToken = assignPropertyToString(environment,
//                "slack.appToken1",
//                "Can't get 'slack.appToken' get it from https://api.slack.com/apps");
//        this.legacyToken = assignPropertyToString(environment,
//                "slack.legacyToken",
//                "Can't get 'slack.legacyToken' get it from https://api.slack.com/custom-integrations/legacy-tokens");
        this.slackWorkspaceUrl = assignPropertyToString(environment,
                "slack.workspace.url",
                "Can't get 'slack.workspace.url' please check slack.properties file");
        this.generalChannelId = assignPropertyToString(environment,
                "slack.workspace.generalChannelId",
                "Can't get 'slack.workspace.generalChannelId' please check slack.properties file");
        this.defaultPrivateGroupNameTemplate = assignPropertyToString(environment,
                "slack.group.default.name.template",
                "Can't get 'slack.group.default.name.template' please check slack.properties file");
        this.studentService = studentService;
        this.clientService = clientService;
        this.socialProfileService = socialProfileService;
        this.projectProperties = projectPropertiesService.getOrCreate();
        this.assignSkypeCallService = assignSkypeCallService;
        this.messageTemplateService = messageTemplateService;
        this.studentRepository = studentRepository;
        this.slackBotUrl = environment.getProperty("slackbot.access") + "://" + environment.getProperty("slackbot.ip")
                + ":" + environment.getProperty("slackbot.port");
    }

    private String assignPropertyToString(Environment environment, String propertyName, String errorText) {
        String result = environment.getProperty(propertyName);
        if (result == null || result.isEmpty()) {
            logger.warn(errorText);
            return StringUtils.EMPTY;
        }
        return result;
    }

//    public void setAppToken(String number, Environment environment) {
//
//        this.appToken = assignPropertyToString(environment,
//                "slack.appToken" + number,
//                "Can't get 'slack.appToken' get it from https://api.slack.com/apps");
//        System.out.println(appToken);
//    }

//    @Override
//    public boolean tryLinkSlackAccountToStudent(long studentId) {
//        Optional<String> allWorkspaceUsersData = receiveAllClientsFromWorkspace();
//        return allWorkspaceUsersData.filter(s -> tryLinkSlackAccountToStudent(studentId, s)).isPresent();
//    }

    @Override
    public void tryLinkSlackAccountToAllStudents() {
        List<Student> empty = studentRepository.getStudentsByClientSocialProfiles_Empty();
        List<SocialNetworkType> excludeSocialProfileTypes = Arrays.asList(SocialNetworkType.SLACK);
        List<Student> students = studentService.getStudentsWithoutSocialProfileByType(excludeSocialProfileTypes);
        students.addAll(empty);
        List<SlackProfile> allClientsFromWorkspace = getAllClientsFromWorkspace();
        for (Student student : students) {
            tryLinkSlackAccountToStudent(student.getId(),allClientsFromWorkspace);
        }
    }
    @Override
    public boolean tryLinkSlackAccountToStudent(long studentId){
        return tryLinkSlackAccountToStudent(studentId,getAllClientsFromWorkspace());
    }

    private boolean tryLinkSlackAccountToStudent(long studentId, List<SlackProfile> profiles) {
        // Weights for matches
        double emailWeight = 1.0d;
        double nameWeight = 0.25d;
        double lastNameWeight = 0.25d;
        // Key = slack user id, Value = match weight
        Map<String, Double> matchesWithWeight = new HashMap<>();
        Student student = studentService.get(studentId);
        Client client = student.getClient();
//        List<SlackProfile> profiles = parseSlackUsersFromJson(slackAllUsersJsonResponse);
//        List<SlackProfile> profiles = getAllClientsFromWorkspace();
        Collections.sort(profiles, Comparator.comparing(SlackProfile::getEmail));
        for (SlackProfile profile : profiles) {
            double currentWeight = 0d;
            String id = profile.getSlackId();
            String name = profile.getName();
            String email = profile.getEmail();
            if (email != null && !email.isEmpty() && client.getEmail().isPresent() && email.equals(client.getEmail().get())) {
                currentWeight += emailWeight;
            }
            if (name != null) {
                if (name.contains(client.getName())) {
                    currentWeight += nameWeight;
                }
                if (name.contains(client.getLastName())) {
                    currentWeight += lastNameWeight;
                }
            }
            if (currentWeight >= nameWeight + lastNameWeight) {
                matchesWithWeight.put(id, currentWeight);
            }
        }
        if (!matchesWithWeight.isEmpty()) {
            Optional<Map.Entry<String, Double>> maximumMatch = matchesWithWeight.entrySet().stream().max(Map.Entry.comparingByValue());
            if (maximumMatch.isPresent()) {

                String slackId = maximumMatch.get().getKey();

                if (!socialProfileService.getSocialProfileBySocialIdAndSocialType(slackId, SocialNetworkType.SLACK.getName()).isPresent()) {
                    client.addSocialProfile(new SocialProfile(slackId, SocialNetworkType.SLACK));
                    clientService.updateClient(client);
                }
                return true;

            }
        }
        return false;
    }

    @Override
    public boolean trySendSlackMessageToStudent(long clientId, String text) {
        Optional<Client> clientOptional = clientService.getClientByID(clientId);
        Optional<AssignSkypeCall> assignSkypeCall = assignSkypeCallService.getAssignSkypeCallByClientId(clientId);
        Client client;
        AssignSkypeCall skypeCall;
        ZonedDateTime zonedDateTime;
        String body = "";
        if (clientOptional.isPresent()) {
            client = clientOptional.get();
            if (assignSkypeCall.isPresent()) {
                skypeCall = assignSkypeCall.get();
                zonedDateTime = skypeCall.getSkypeCallDate();
                body = zonedDateTime.format(DateTimeFormatter.ofPattern("dd.MM.YY в HH-mm"));
            }
            List<SocialProfile> profiles = client.getSocialProfiles();
            for (SocialProfile socialProfile : profiles) {
                if ("slack".equals(socialProfile.getSocialNetworkType().getName())) {
                    return trySendMessageToSlackUser(socialProfile.getSocialId(), messageTemplateService.prepareText(client, text, body));
                }
            }
        }
        return false;
    }

    @Override
    public Optional<String> getAllEmailsFromSlack() {
        StringBuilder result = new StringBuilder();
//            List<SlackProfile> slackProfiles = parseSlackUsersFromJson(json.get());
        List<SlackProfile> slackProfiles = getAllClientsFromWorkspace();
        for (SlackProfile profile : slackProfiles) {
            String email = profile.getEmail();
            if (email != null && !email.isEmpty()) {
                result.append(email).append("\n");
            }
        }
        return result.toString().isEmpty() ? Optional.empty() : Optional.of(result.toString());
    }

    @Override
    public Optional<String> getAllIdsFromSlack() {
        StringBuilder result = new StringBuilder();
//            List<SlackProfile> slackProfiles = parseSlackUsersFromJson(json.get());
        List<SlackProfile> slackProfiles = getAllClientsFromWorkspace();
        for (SlackProfile profile : slackProfiles) {
            if (profile.getSlackId() != null && !profile.getSlackId().isEmpty()) {
                result.append(profile.getSlackId()).append("\n");
            }
        }

        return result.toString().isEmpty() ? Optional.empty() : Optional.of(result.toString());
    }

    @Override
    public boolean trySendMessageToAllSlackUsers(String text) {
        boolean result = false;
//            List<SlackProfile> slackProfiles = parseSlackUsersFromJson(json.get());
        List<SlackProfile> slackProfiles = getAllClientsFromWorkspace();
        result = !slackProfiles.isEmpty();
        for (SlackProfile profile : slackProfiles) {
            result &= trySendMessageToSlackUser(profile.getSlackId(), text);
        }
        return result;
    }

    @Override
    public boolean trySendMessageToAllStudents(String text) {
        List<Student> students = studentService.getAll();
        boolean result = !students.isEmpty();
        for (Student student : students) {
            result &= trySendSlackMessageToStudent(student.getId(), text);
        }
        return result;
    }

//    private Optional<String> receiveAllClientsFromWorkspace() {
//        String url = SLACK_API_URL + "users.list" +
//                "?token=" + appToken;
//        try (CloseableHttpClient client = HttpClients.createDefault();
//             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
//            HttpEntity entity = response.getEntity();
//            return Optional.ofNullable(EntityUtils.toString(entity));
//        } catch (IOException e) {
//            logger.error("Can't get data from Slack server", e);
//        }
//        return Optional.empty();
//    }

    public List<SlackProfile> getAllClientsFromWorkspace() {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<SlackProfile[]> forEntity = restTemplate.getForEntity(slackBotUrl + "/bot/users/all", SlackProfile[].class);
        if (forEntity.getStatusCode().is2xxSuccessful()) {
            return Arrays.asList(Objects.requireNonNull(forEntity.getBody()));
        }
        return Collections.emptyList();
    }

    @Override
    public boolean trySendMessageToSlackUser(String slackUserId, String text) {
        Optional<String> chatId = getChatIdForSlackUser(slackUserId);
        return chatId.filter(s -> trySendMessageToSlackChannel(s, text)).isPresent();
    }

    private String trimStringToLength(String input) {
        String output = input.trim().replaceAll(" ", "");
        if (output.length() > 9) {
            output = output.substring(0, 9);
        }
        return output;
    }

    private String correctChannelNameIfTooLong(String channelName) {
        return channelName.length() > 22 ? channelName.substring(0, 22) : channelName;
    }

    private Optional<String> createPrivateChannel(String name, String lastName, boolean firstAttempt) {
        String json = StringUtils.EMPTY;
//        String channelName = String.format(defaultPrivateGroupNameTemplate, trimStringToLength(name), trimStringToLength(lastName));
        String channelName = correctChannelNameIfTooLong(defaultPrivateGroupNameTemplate + "_" + name + "_" + lastName);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> forEntity = restTemplate.getForEntity(slackBotUrl + "/channel/create?name=" + channelName, String.class);
        if (forEntity.getStatusCode().is2xxSuccessful()) {
            logger.info("channel with name " + channelName + " created");
            return Optional.ofNullable(forEntity.getBody());

        } else {
            logger.error("channel with name " + channelName + "can`t be created");
        }
        //todo что это за рекурсия для чего она нужна (просто захардкожена)
        if (firstAttempt) {
            return createPrivateChannel(lastName, name, false);
        }
        return Optional.of(forEntity.getStatusCode().toString());

//
//        try {
//            channelName = URLEncoder.encode(channelName, URL_ENCODING_CHARSET);
//        } catch (UnsupportedEncodingException e) {
//            logger.error("Can't encode Slack channel name " + channelName, e);
//        }
//        String url = SLACK_API_URL + "groups.create" +
//                "?token=" + appToken +
//                "&name=" + channelName;
//        try (CloseableHttpClient client = HttpClients.createDefault();
//             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
//            HttpEntity entity = response.getEntity();
//            json = EntityUtils.toString(entity);
//            JSONObject jsonObj = new JSONObject(json);
//            JSONObject groupData = jsonObj.optJSONObject("group");
//            if (groupData != null) {
//                return Optional.ofNullable(groupData.optString("id"));
//            }
//        } catch (IOException e) {
//            logger.error("Can't get slack group id", e);
//        } catch (JSONException e) {
//            logger.error(String.format("Can't parse slack group id json = %s", json), e);
//        }
//        if (firstAttempt) {
//            return createPrivateChannel(lastName, name, false);
//        }
//        return Optional.empty();
    }

    private void inviteDefaultUsersToChannel(String channelId) {
        String defaultUsers = projectProperties.getSlackDefaultUsers();
        if (defaultUsers != null && !defaultUsers.isEmpty()) {
            String[] users = defaultUsers.split(" ");
            for (String userId : users) {
                inviteUserToChannel(userId, channelId);
//                String url = SLACK_API_URL + "groups.invite" +
//                        "?token=" + appToken +
//                        "&channel=" + channelId +
//                        "&user=" + userId;
//                try (CloseableHttpClient client = HttpClients.createDefault();
//                     CloseableHttpResponse response = client.execute(new HttpGet(url))) {
//                    HttpEntity entity = response.getEntity();
//                    logger.debug(String.format("Default Slack user %s has been invited to channel with response %s", userId, EntityUtils.toString(entity)));
//                } catch (IOException e) {
//                    logger.error(String.format("Can't invite default user %s to channel %s", userId, channelId), e);
//                }
            }
        }
    }

    private void inviteUserToChannel(String userId, String channelId) {
        ResponseEntity<String> forEntity = new RestTemplate().getForEntity(slackBotUrl + "/channel/user/invite?" + "channel_id=" + channelId + "&user_id=" + userId, String.class);
        if (forEntity.getStatusCode().is2xxSuccessful()) {
            logger.info("user with id :" + userId + " was added to channel with id :" + channelId);
        } else {
            logger.error("user with id :" + userId + " can`t be added to channel with id :" + channelId);
        }
    }

    @Override
    public ResponseEntity<String> inviteToWorkspace(String name, String lastName, String email) {
//        String json = StringUtils.EMPTY;
        String channels = generalChannelId;
        Optional<String> privateChannelId = createPrivateChannel(name, lastName, true);
        if (privateChannelId.isPresent()) {
            channels += "," + privateChannelId.get();
            inviteDefaultUsersToChannel(channels);
        } else {
            logger.warn("can`t create channel with id :" + privateChannelId.orElse("null"));
        }
        return inviteToWorkspace(email, channels, name, lastName);


//        name = name.trim().replaceAll(" ", "");
//        lastName = lastName.trim().replaceAll(" ", "");
//        try {
//            name = URLEncoder.encode(name, URL_ENCODING_CHARSET);
//            lastName = URLEncoder.encode(lastName, URL_ENCODING_CHARSET);
//        } catch (UnsupportedEncodingException e) {
//            logger.error(String.format("Can't encode Slack name = %s lastName = %s ", name, lastName), e);
//        }
//        String url = SLACK_API_URL + "users.admin.invite" +
//                "?token=" + legacyToken +
//                "&email=" + email +
//                "&first_name=" + name +
//                "&last_name=" + lastName +
//                "&channels=" + channels;
//        try (CloseableHttpClient client = HttpClients.createDefault();
//             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
//            HttpEntity entity = response.getEntity();
//            json = EntityUtils.toString(entity);
//            JSONObject jsonObj = new JSONObject(json);
//            if (!jsonObj.optBoolean("ok")) {
//                logger.error(jsonObj.toString());
//            }
//            return jsonObj.optBoolean("ok");
//        } catch (IOException e) {
//            logger.error("Can't get response when inviting user to Slack", e);
//        } catch (JSONException e) {
//            logger.error(String.format("Can't parse response when inviting user to Slack, json = %s", json), e);
//        }
//        return false;
    }

    @Override
    public ResponseEntity<String> inviteToWorkspace(String email) {
        return inviteToWorkspace(email, generalChannelId);
    }

    private ResponseEntity<String> inviteToWorkspace(String email, String channelIds) {
        String url = slackBotUrl + "/bot/user/workspace/invite?" + "email=" + email + "&channels=" + channelIds;

        return getStringResponseEntity(url);
    }

    private ResponseEntity<String> getStringResponseEntity(String url) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForEntity(url, String.class);

        } catch (HttpStatusCodeException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<String> inviteToWorkspace(String email, String channelIds, String name, String lastName) {
        String url = slackBotUrl + "/bot/user/workspace/invite?" + "email=" + email + "&channels=" + channelIds + "&name=" + name + "&last_name=" + lastName;
        RestTemplate restTemplate = new RestTemplate();
        return getStringResponseEntity(url);
    }

    @Override
    public Optional<String> getChatIdForSlackUser(String slackUserId) {
        String url = slackBotUrl + "/bot/user/channel?" + "slack_user_id=" + slackUserId;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        if (forEntity.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(forEntity.getBody());
        } else {
            return Optional.empty();
        }


//        String json = StringUtils.EMPTY;
//        String url = SLACK_API_URL + "im.open" +
//                "?token=" + appToken +
//                "&user=" + slackUserId +
//                "&return_im=true";
//        try (CloseableHttpClient client = HttpClients.createDefault();
//             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
//            HttpEntity entity = response.getEntity();
//            json = EntityUtils.toString(entity);
//            JSONObject jsonObj = new JSONObject(json);
//            JSONObject chatData = jsonObj.optJSONObject("channel");
//            if (chatData != null) {
//                return Optional.ofNullable(chatData.optString("id"));
//            }
//        } catch (IOException e) {
//            logger.error("Can't get slack chat id for user id " + slackUserId, e);
//        } catch (JSONException e) {
//            logger.error(String.format("Can't parse slack chat id for user id %s json = %s", slackUserId, json), e);
//        }
//        return Optional.empty();
    }

    private boolean trySendMessageToSlackChannel(String channelId, String text) {
        String url = slackBotUrl + "/bot/slack/message/send?" + "message=" + text + "&channel_id=" + channelId + "&admin_tag=true";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        boolean xxSuccessful = response.getStatusCode().is2xxSuccessful();
        if (!xxSuccessful) {
            logger.error("Message to Slack channel " + channelId + " don't sending ");
        }
        return xxSuccessful;

//        try {
//            url = SLACK_API_URL + "chat.postMessage" +
//                    "?token=" + appToken +
//                    "&channel=" + channelId +
//                    "&text=" + URLEncoder.encode(text, URL_ENCODING_CHARSET) +
//                    "&as_user=true";
//        } catch (UnsupportedEncodingException e) {
//            logger.error("Can't format URL for Slack post message request", e);
//            return false;
//        }
//        try (CloseableHttpClient client = HttpClients.createDefault();
//             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
//            HttpEntity entity = response.getEntity();
//            json = EntityUtils.toString(entity);
//            JSONObject jsonObj = new JSONObject(json);
//            String sendResult = jsonObj.optString("ok");
//            if ("true".equals(sendResult)) {
//                logger.debug("Message to Slack channel " + channelId + " sending!");
//                return true;
//            } else if ("false".equals(sendResult)) {
//                logger.error("Message to Slack channel " + channelId + " don't sending because " + jsonObj.toString());
//            }
//        } catch (IOException e) {
//            logger.error("Can't post message to Slack channel " + channelId, e);
//        } catch (JSONException e) {
//            logger.error(String.format("Can't parse result of sending message to Slack channel %s json = %s", channelId, json), e);
//        }
//        return false;
    }

//    private List<SlackProfile> parseSlackUsersFromJson(String json) {
//        List<SlackProfile> result = new ArrayList<>();
//        try {
//            JSONObject jsonObj = new JSONObject(json);
//            JSONArray jsonData = jsonObj.getJSONArray("members");
//            for (int i = 0; i < jsonData.length(); i++) {
//                JSONObject current = jsonData.getJSONObject(i);
//                JSONObject userProfile = current.optJSONObject("profile");
//                if (userProfile != null) {
//                    String id = current.optString("id");
//                    String mail = userProfile.optString("email");
//                    String name = userProfile.optString("real_name");
//                    result.add(new SlackProfile(id, mail, name));
//                }
//            }
//        } catch (JSONException e) {
//            logger.error("Can't parse users from slack json = " + json, e);
//        }
//        return result;
//    }

    public String getSlackWorkspaceUrl() {
        return slackWorkspaceUrl;
    }

    private static class SlackProfile {
        private String slackId;
        private String email;
        private String name;

        private SlackProfile() {
        }

        private SlackProfile(String slackId, String email, String name) {
            this.slackId = slackId;
            this.email = email;
            this.name = name;
        }

        private String getSlackId() {
            return slackId;
        }

        private void setSlackId(String slackId) {
            this.slackId = slackId;
        }

        private String getEmail() {
            return email;
        }

        private void setEmail(String email) {
            this.email = email;
        }

        private String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "SlackProfile{" +
                    "slackId='" + slackId + '\'' +
                    ", email='" + email + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }


}

