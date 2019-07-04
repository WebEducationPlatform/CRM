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
    }

    private void inviteDefaultUsersToChannel(String channelId) {
        String defaultUsers = projectProperties.getSlackDefaultUsers();
        if (defaultUsers != null && !defaultUsers.isEmpty()) {
            String[] users = defaultUsers.split(" ");
            for (String userId : users) {
                inviteUserToChannel(userId, channelId);
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
        String channels = generalChannelId;
        Optional<String> privateChannelId = createPrivateChannel(name, lastName, true);
        if (privateChannelId.isPresent()) {
            channels += "," + privateChannelId.get();
            inviteDefaultUsersToChannel(channels);
        } else {
            logger.warn("can`t create channel with id :" + privateChannelId.orElse("null"));
        }
        return inviteToWorkspace(email, channels, name, lastName);


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

    }


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

