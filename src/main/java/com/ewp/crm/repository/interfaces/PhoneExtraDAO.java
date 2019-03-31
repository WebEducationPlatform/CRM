package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.PhoneExtra;
import java.util.List;

public interface PhoneExtraDAO extends CommonGenericRepository<PhoneExtra> {
    List<PhoneExtra> getAllByClient(Client client);
}
