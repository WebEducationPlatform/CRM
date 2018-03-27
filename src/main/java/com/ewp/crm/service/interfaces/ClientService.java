package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.models.User;

import java.util.List;


public interface ClientService {

	List<Client> getAllClients();

	List<Client> getClientsByOwnerUser(User ownerUser);

	Client getClientByEmail(String name);

	Client getClientByID(Long id);

	Client getClientByVkId (Long vkId);

	void addClient(Client client);

	void updateClient(Client client);

	void deleteClient(Long id);

	void deleteClient(Client client);

	List<Client> filteringClient(FilteringCondition filteringCondition);
}
