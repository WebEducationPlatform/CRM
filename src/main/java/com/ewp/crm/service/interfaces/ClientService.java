package com.ewp.crm.service.interfaces;

import com.ewp.crm.exceptions.client.ClientException;
import com.ewp.crm.models.Client;

import java.util.List;


public interface ClientService {
	List<Client> getAllClients();

	Client getClientByEmail(String name) throws ClientException;

	Client getClientByID(Long id) throws ClientException;

	void addClient(Client client) throws ClientException;

	void updateClient(Long id, Client client) throws ClientException;

	void deleteClient(Client client) throws ClientException;

	void deleteClient(Long id) throws ClientException;

}
