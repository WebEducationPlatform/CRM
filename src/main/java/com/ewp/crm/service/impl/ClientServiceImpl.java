package com.ewp.crm.service.impl;


import com.ewp.crm.exceptions.client.ClientException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.repository.interfaces.ClientDAO;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {


	@Autowired
	ClientDAO clientDAO;

	@Autowired
	StatusDAO statusDAO;

	@Override
	public List<Client> getAllClients() {
		return clientDAO.findAll();
	}

	@Override
	public Client getClientByEmail(String email) throws ClientException {
		Client client = clientDAO.findClientByEmail(email);
		if(client==null){
			throw new ClientException("There is no client with email " + email);
		}
		return client;
	}

	@Override
	public Client getClientByID(Long id) throws ClientException {
		Client client = clientDAO.findOne(id);
		//if(client==null){
		//	throw new ClientException("There is no client with id " + id);
		//}
		return client;
	}

	@Override
	public void deleteClient(Long id) throws ClientException {
		Client currentClient = clientDAO.findOne(id);
		if (currentClient == null) {
			throw new ClientException("Client with id " + id + " not found");
		}
		clientDAO.delete(id);
	}

	@Override
	public void addClient(Client client) throws ClientException {
		Client currentClient = clientDAO.findClientByEmail(client.getEmail());
		if (currentClient != null) {
			throw new ClientException("Client already exists");
		}
		if (client.getStatus() == null) {
			throw new ClientException("Client status is null");
		}
		Status stat;
		if ((stat = statusDAO.findStatusByName(client.getStatus().getName())) != null) {
			client.setStatus(stat);
		}
		clientDAO.saveAndFlush(client);
	}

	@Override
	public void updateClient(Client client) {

	}

	@Override
	public void updateClient(Long id, Client client) throws ClientException {
		Client currentClient = clientDAO.findOne(id);
		if (currentClient == null) {
			throw new ClientException("Client with id " + id + " not found");
		}
		Status stat;
		if ((stat = statusDAO.findStatusByName(client.getStatus().getName())) != null) {
			client.setStatus(stat);
		}
		client.setId(currentClient.getId());
		clientDAO.saveAndFlush(client);
	}

	@Override
	public void deleteClient(Client client) {
		clientDAO.delete(client);
	}
}
