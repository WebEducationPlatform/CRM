package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.MessageTemplate;

import java.util.Map;

public interface MessageTemplateService extends CommonService<MessageTemplate> {
    MessageTemplate getByName(String name);

    String replaceName(String msg, Map<String, String> params);
}
