package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;

import java.util.List;


public interface ClientService {

	List<Client> getAllClients();

	Client getClientByEmail(String name);

	Client getClientByID(Long id);

	void addClient(Client client);

	void updateClient(Client client);

	void deleteClient(Long id);

	void deleteClient(Client client);
}
