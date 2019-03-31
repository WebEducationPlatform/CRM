package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.PhoneExtra;

import java.util.List;

public interface PhoneExtraService extends CommonService<PhoneExtra>{

    List<PhoneExtra> getAllPhonesExtraByClient(Client client);
}
