package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.FilteringCondition;

import java.util.List;

public interface ClientRepositoryCustom {

	List filteringClient(FilteringCondition filteringCondition);

	List<Client> getChangeActiveClients();

	List getClientsEmail();

	List<String> getClientsPhoneNumber();

	List<String> getFilteredClientsEmail(FilteringCondition filteringCondition);

	List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition);

	List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition);

}
