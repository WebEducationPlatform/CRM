package com.ewp.crm.service.slack;

import com.ewp.crm.service.interfaces.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Service;

@Service
@PropertySources(value = {
        @PropertySource("classpath:application.properties"),
        @PropertySource("file:./slack.properties")
})
public class SlackServiceImpl implements SlackService {

    private static Logger logger = LoggerFactory.getLogger(SlackServiceImpl.class);

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
