package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.MessageTemplate;

import java.util.Map;
import java.util.Optional;

public interface MessageTemplateService extends CommonService<MessageTemplate> {
    Optional<MessageTemplate> getByName(String name);

    String replaceName(String msg, Map<String, String> params);
    String prepareText(Client client, String templateText, String body);
}
