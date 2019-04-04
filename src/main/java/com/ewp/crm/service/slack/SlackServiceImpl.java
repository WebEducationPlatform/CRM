package com.ewp.crm.service.slack;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfileType;
import com.ewp.crm.models.Student;
import com.ewp.crm.service.interfaces.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Service
@PropertySources(value = {
        @PropertySource("classpath:application.properties"),
        @PropertySource("file:./slack.properties")
})
public class SlackServiceImpl implements SlackService {

    private static final String SLACK_API_URL = "https://slack.com/api/";
    private static Logger logger = LoggerFactory.getLogger(SlackServiceImpl.class);
    private final StudentService studentService;
    private final SocialProfileTypeService socialProfileTypeService;
    private final SocialProfileService socialProfileService;
    private final ClientService clientService;
    private final String slackWorkspaceUrl;
    private final String inviteToken;

    @Autowired
    public SlackServiceImpl(Environment environment, StudentService studentService, SocialProfileTypeService socialProfileTypeService, ClientService clientService, SocialProfileService socialProfileService) {
        this.inviteToken = assignPropertyToString(environment,
                "slack.legacyToken",
                    "Can't get 'slack.legacyToken' get it from https://api.slack.com/custom-integrations/legacy-tokens");
        this.slackWorkspaceUrl = assignPropertyToString(environment,
                "slack.workspace.url",
                    "Can't get 'slack.workspace.url' please check slack.properties file");
        this.studentService = studentService;
        this.socialProfileTypeService = socialProfileTypeService;
        this.clientService = clientService;
        this.socialProfileService = socialProfileService;
    }

    private String assignPropertyToString(Environment environment, String propertyName, String errorText) {
        String result = environment.getProperty(propertyName);
        if (result == null || result.isEmpty()) {
            logger.warn(errorText);
            return "";
        }
        return result;
    }

    @Override
    public boolean tryLinkSlackAccountToStudent(long studentId) {
        Optional<String> allWorkspaceUsersData = receiveAllClientsFromWorkspace();
        return allWorkspaceUsersData.filter(s -> tryLinkSlackAccountToStudent(studentId, s)).isPresent();
    }

    @Override
    public void tryLinkSlackAccountToAllStudents() {
        Optional<String> allWorkspaceUsersData = receiveAllClientsFromWorkspace();
        List<Client> clients = clientService.getAllClients();
        if (allWorkspaceUsersData.isPresent()) {
            for (Client client : clients) {
                if (client.getStudent() != null) {
                    boolean hasSlackId = false;
                    for (SocialProfile profile : client.getSocialProfiles()) {
                        if ("slack".equals(profile.getSocialProfileType().getName())) {
                            hasSlackId = true;
                            break;
                        }
                    }
                    if (!hasSlackId) {
                        tryLinkSlackAccountToStudent(client.getStudent().getId(), allWorkspaceUsersData.get());
                    }
                }
            }
        }
    }

    @Override
    public boolean tryLinkSlackAccountToStudent(long studentId, String slackAllUsersJsonResponse) {
        // Weights for matches
        double emailWeight = 1.0d;
        double nameWeight = 0.25d;
        double lastNameWeight = 0.25d;
        // Key = slack user id, Value = match weight
        Map<String, Double> matchesWithWeight = new HashMap<>();
        Student student = studentService.get(studentId);
        Client client = student.getClient();
        List<SlackProfile> profiles = parseSlackUsersFromJson(slackAllUsersJsonResponse);
        for (SlackProfile profile :profiles) {
            double currentWeight = 0d;
            String id = profile.id;
            String name = profile.name;
            String email = profile.mail;
            if (email != null && !email.isEmpty() && email.equals(client.getEmail())) {
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
                Optional<SocialProfileType> slackSocialProfileTypeOpt = socialProfileTypeService.getByTypeName("slack");
                if (slackSocialProfileTypeOpt.isPresent()) {
                    String slackId = maximumMatch.get().getKey();
                    SocialProfileType slackSocialProfileType = slackSocialProfileTypeOpt.get();
                    if (!socialProfileService.getSocialProfileBySocialIdAndSocialType(slackId, slackSocialProfileType.getName()).isPresent()) {
                        client.addSocialProfile(new SocialProfile(slackId, slackSocialProfileType));
                        clientService.updateClient(client);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean trySendSlackMessageToStudent(long studentId, String text) {
        Student student = studentService.get(studentId);
        if (student != null) {
            Client client = student.getClient();
            List<SocialProfile> profiles = client.getSocialProfiles();
            for (SocialProfile socialProfile :profiles) {
                if ("slack".equals(socialProfile.getSocialProfileType().getName())) {
                    return trySendMessageToSlackUser(socialProfile.getSocialId(), text);
                }
            }
            if (tryLinkSlackAccountToStudent(studentId)) {
                return trySendSlackMessageToStudent(studentId, text);
            }
        }
        return false;
    }

    @Override
    public Optional<String> getAllEmailsFromSlack() {
        Optional<String> json = receiveAllClientsFromWorkspace();
        StringBuilder result = new StringBuilder();
        if (json.isPresent()) {
            List<SlackProfile> slackProfiles = parseSlackUsersFromJson(json.get());
            for (SlackProfile profile : slackProfiles) {
                String email = profile.mail;
                if (email != null && !email.isEmpty()) {
                    result.append(email).append("\n");
                }
            }
        }
        return result.toString().isEmpty() ? Optional.empty() : Optional.of(result.toString());
    }

    @Override
    public Optional<String> getAllIdsFromSlack() {
        Optional<String> json = receiveAllClientsFromWorkspace();
        StringBuilder result = new StringBuilder();
        if (json.isPresent()) {
            List<SlackProfile> slackProfiles = parseSlackUsersFromJson(json.get());
            for (SlackProfile profile : slackProfiles) {
                if (profile.id != null && !profile.id.isEmpty()) {
                    result.append(profile.id).append("\n");
                }
            }
        }
        return result.toString().isEmpty() ? Optional.empty() : Optional.of(result.toString());
    }

    @Override
    public boolean trySendMessageToAllSlackUsers(String text) {
        Optional<String> json = receiveAllClientsFromWorkspace();
        boolean result = false;
        if (json.isPresent()) {
            List<SlackProfile> slackProfiles = parseSlackUsersFromJson(json.get());
            result = !slackProfiles.isEmpty();
            for (SlackProfile profile : slackProfiles) {
                result &= trySendMessageToSlackUser(profile.id, text);
            }
        }
        return result;
    }

    @Override
    public boolean trySendMessageToAllStudents(String text) {
        List<Student> students = studentService.getAll();
        boolean result = !students.isEmpty();
        for (Student student :students) {
            result &= trySendSlackMessageToStudent(student.getId(), text);
        }
        return result;
    }

    private Optional<String> receiveAllClientsFromWorkspace() {
        String url = SLACK_API_URL + "users.list" +
                "?token=" + inviteToken;
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            return Optional.ofNullable(EntityUtils.toString(entity));
        } catch (IOException e) {
            logger.error("Can't get data from Slack server", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean trySendMessageToSlackUser(String slackUserId, String text) {
        Optional<String> chatId = getChatIdForSlackUser(slackUserId);
        return chatId.filter(s -> trySendMessageToSlackChannel(s, text)).isPresent();
    }

    private Optional<String> getChatIdForSlackUser(String slackUserId) {
        String url = SLACK_API_URL + "im.open" +
                "?token=" + inviteToken +
                "&user=" + slackUserId +
                "&return_im=true";
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            JSONObject jsonObj = new JSONObject(EntityUtils.toString(entity));
            JSONObject chatData = jsonObj.optJSONObject("channel");
            if (chatData != null) {
                return Optional.ofNullable(chatData.optString("id"));
            }
        } catch (IOException e) {
            logger.error("Can't get slack chat id for user id " + slackUserId, e);
        } catch (JSONException e) {
            logger.error("Can't parse slack chat id for user id " + slackUserId, e);
        }
        return Optional.empty();
    }

    private boolean trySendMessageToSlackChannel(String channelId, String text) {
        String url;
        try {
            url = SLACK_API_URL + "chat.postMessage" +
                    "?token=" + inviteToken +
                    "&channel=" + channelId +
                    "&text=" + URLEncoder.encode(text, "UTF-8") +
                    "&as_user=true";
        } catch (UnsupportedEncodingException e) {
            logger.error("Can't format URL for Slack post message request", e);
            return false;
        }
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            JSONObject jsonObj = new JSONObject(EntityUtils.toString(entity));
            String sendResult = jsonObj.optString("ok");
            if ("true".equals(sendResult)) {
                return true;
            }
        } catch (IOException e) {
            logger.error("Can't post message to Slack channel " + channelId, e);
        } catch (JSONException e) {
            logger.error("Can't parse result of sending message to Slack channel " + channelId, e);
        }
        return false;
    }

    private List<SlackProfile> parseSlackUsersFromJson(String json) {
        List<SlackProfile> result = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonData = jsonObj.getJSONArray("members");
            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject current = jsonData.getJSONObject(i);
                JSONObject userProfile = current.optJSONObject("profile");
                if (userProfile != null) {
                    String id = current.optString("id");
                    String mail = userProfile.optString("email");
                    String name = userProfile.optString("real_name");
                    result.add(new SlackProfile(id, mail, name));
                }
            }
        } catch (JSONException e) {
            logger.error("Can't parse users from slack", e);
        }
        return result;
    }

    public String getSlackWorkspaceUrl() {
        return slackWorkspaceUrl;
    }

    private class SlackProfile {
        private String id;
        private String mail;
        private String name;

        private SlackProfile(String id, String mail, String name) {
            this.id = id;
            this.mail = mail;
            this.name = name;
        }
    }

}
