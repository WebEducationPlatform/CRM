package com.ewp.crm.service.slack;

import com.ewp.crm.service.impl.StudentServiceImpl;
import com.ewp.crm.service.interfaces.*;
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

@Service
@PropertySources(value = {
        @PropertySource("classpath:application.properties"),
        @PropertySource("file:./slack.properties")
})
public class SlackServiceImpl implements SlackService {

    private static Logger logger = LoggerFactory.getLogger(SlackServiceImpl.class);

    private final ClientService clientService;
    private final StudentServiceImpl studentService;
    private final StatusService statusService;
    private final ClientHistoryService clientHistoryService;
    private final ProjectPropertiesService propertiesService;

    // get it for you Workspace from
    // https://api.slack.com/custom-integrations/legacy-tokens
    // and put in to slack.properties
    private String LEGACY_TOKEN;

    @Autowired
    public SlackServiceImpl(Environment environment,
                            ClientService clientService,
                            StudentServiceImpl studentService,
                            StatusService statusService,
                            ClientHistoryService clientHistoryService,
                            ProjectPropertiesService propertiesService) {
        try {
            this.LEGACY_TOKEN = environment.getRequiredProperty("slack.legacyToken");
            if (LEGACY_TOKEN.isEmpty()) {
                throw new NullPointerException();
            }
        } catch (NullPointerException npe) {
            logger.error("Can't get slack.legacyToken get it from https://api.slack.com/custom-integrations/legacy-tokens", npe);
        }
        this.clientService = clientService;
        this.studentService = studentService;
        this.statusService = statusService;
        this.clientHistoryService = clientHistoryService;
        this.propertiesService = propertiesService;
    }

    @Override
    public String getEmailListFromJson(String json) {
        try {
            StringBuilder result = new StringBuilder();
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonData = jsonObj.getJSONArray("members");
            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject userProfile = jsonData.getJSONObject(i).optJSONObject("profile");
                if (userProfile == null) {
                    continue;
                }
                String mail = userProfile.optString("email");
                if (mail != null && !mail.isEmpty()) {
                    result.append(mail);
                    if (i != jsonData.length() - 1) {
                        result.append("\n");
                    }
                }
            }
            return result.toString();
        } catch (JSONException e) {
            logger.warn("Can't parse emails from slack", e);
        }
        return "Error";
    }
}
