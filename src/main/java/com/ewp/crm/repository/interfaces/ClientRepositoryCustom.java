package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;

import java.util.List;

public interface ClientRepositoryCustom {

    List<Client> customQuery(String query);
}
