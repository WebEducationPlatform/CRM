package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.EmailExtra;

import java.util.List;

public interface EmailExtraService extends CommonService<EmailExtra>{

    List<EmailExtra> getAllEmailsExtraByClient(Client client);
}
