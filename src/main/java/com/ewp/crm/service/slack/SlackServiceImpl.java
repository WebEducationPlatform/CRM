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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final String inviteToken;

    @Autowired
    public SlackServiceImpl(Environment environment, StudentService studentService, SocialProfileTypeService socialProfileTypeService, ClientService clientService, SocialProfileService socialProfileService) {
        this.inviteToken = environment.getProperty("slack.legacyToken");
        if (inviteToken == null || inviteToken.isEmpty()) {
            logger.warn("Can't get slack.legacyToken get it from https://api.slack.com/custom-integrations/legacy-tokens");
        }
        this.studentService = studentService;
        this.socialProfileTypeService = socialProfileTypeService;
        this.clientService = clientService;
        this.socialProfileService = socialProfileService;
    }

    @Override
    public boolean tryLinkSlackAccountToStudent(long studentId) {
        Optional<String> allWorkspaceUsersData = receiveAllClientsFromWorkspace();
        return allWorkspaceUsersData.filter(s -> trySendSlackMessageToStudent(studentId, s)).isPresent();
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
    public boolean tryLinkSlackAccountToStudent(long studentId, String usersData) {
        double emailWeight = 1.0d;
        double nameWeight = 0.25d;
        double lastNameWeight = 0.25d;
        Map<String, Double> result = new HashMap<>();
        Student student = studentService.get(studentId);
        Map<String, String[]> data = parseSlackUsersFromJson(usersData);
        for (Map.Entry<String, String[]> entry :data.entrySet()) {
            double weight = 0d;
            String id = entry.getKey();
            String name = entry.getValue()[0];
            String email = entry.getValue()[1];
            if (email != null && !email.isEmpty() && email.equals(student.getClient().getEmail())) {
                weight += emailWeight;
            }
            if (name != null) {
                if (name.contains(student.getClient().getName())) {
                    weight += nameWeight;
                }
                if (name.contains(student.getClient().getLastName())) {
                    weight += lastNameWeight;
                }
            }
            if (weight >= nameWeight + lastNameWeight) {
                result.put(id, weight);
            }
        }
        if (!result.isEmpty()) {
            Optional<Map.Entry<String, Double>> entry = result.entrySet().stream().max(Map.Entry.comparingByValue());
            if (entry.isPresent()) {
                Optional<SocialProfileType> slackSocialProfile = socialProfileTypeService.getByTypeName("slack");
                if (slackSocialProfile.isPresent()) {
                    if (!socialProfileService.getSocialProfileBySocialIdAndSocialType(entry.get().getKey(), slackSocialProfile.get().getName()).isPresent()) {
                        student.getClient().addSocialProfile(new SocialProfile(entry.get().getKey(), slackSocialProfile.get()));
                        clientService.updateClient(student.getClient());
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
            Map<String, String[]> data = parseSlackUsersFromJson(json.get());
            for (Map.Entry<String, String[]> entry : data.entrySet()) {
                String email = entry.getValue()[1];
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
            Map<String, String[]> data = parseSlackUsersFromJson(json.get());
            for (String id : data.keySet()) {
                if (id != null && !id.isEmpty()) {
                    result.append(id).append("\n");
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
            Map<String, String[]> data = parseSlackUsersFromJson(json.get());
            result = !data.isEmpty();
            for (String id : data.keySet()) {
                result &= trySendMessageToSlackUser(id, text);
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

    /**
     * Get slack users data and put it to map with user 'id' as a key and array [email, name] as value
     */
    private Map<String, String[]> parseSlackUsersFromJson(String json) {
        Map<String, String[]> result = new HashMap<>();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonData = jsonObj.getJSONArray("members");
            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject current = jsonData.getJSONObject(i);
                JSONObject userProfile = current.optJSONObject("profile");
                if (userProfile == null) {
                    continue;
                }
                String id = current.optString("id");
                String mail = userProfile.optString("email");
                String name = userProfile.optString("real_name");
                result.put(id, new String[]{name, mail});
            }
        } catch (JSONException e) {
            logger.error("Can't parse users from slack", e);
        }
        return result;
    }

}
