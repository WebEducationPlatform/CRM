package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.FilteringCondition;

import java.util.List;

public interface ClientRepositoryCustom {

    List<Client> filteringClient(FilteringCondition filteringCondition);
}
