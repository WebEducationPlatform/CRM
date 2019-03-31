package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.EmailExtra;

import java.util.List;

public interface EmailExtraDAO extends CommonGenericRepository<EmailExtra> {
    List<EmailExtra> getAllByClient(Client client);
}
