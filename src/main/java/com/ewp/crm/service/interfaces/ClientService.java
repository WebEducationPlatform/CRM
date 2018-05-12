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

	void addClient(Client client);

	void updateClient(Client client);

	void deleteClient(Long id);

	void deleteClient(Client client);

	List<Client> filteringClient(FilteringCondition filteringCondition);

	List<Client> getChangeActiveClients();

	List<String> getClientsEmails();

	List<String> getClientsPhoneNumbers();

	List<String> getFilteredClientsEmail(FilteringCondition filteringCondition);

	List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition);

	List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition);
}