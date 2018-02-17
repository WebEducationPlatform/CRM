package com.ewp.crm.service.impl;


import com.ewp.crm.exceptions.client.ClientException;
import com.ewp.crm.models.Client;
import com.ewp.crm.repository.interfaces.ClientDAO;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {


	@Autowired
	private ClientDAO clientDAO;

	@Autowired
	private StatusDAO statusDAO;

	@Override
	public List<Client> getAllClients() {
		return clientDAO.findAll();
	}

	@Override
	public Client getClientByEmail(String email) throws ClientException {
		return clientDAO.findClientByEmail(email);
	}

	@Override
	public Client getClientByID(Long id) throws ClientException {
		return clientDAO.findOne(id);
	}

	@Override
	public void deleteClient(Long id) throws ClientException {
		try {
			clientDAO.delete(id);
		} catch (Exception e) {
			throw new ClientException("An error occurred while deleting the client");
		}
	}

	@Override
	public void addClient(Client client) throws ClientException {
		try {
			client.setStatus(statusDAO.findStatusByName(client.getStatus().getName()));
			clientDAO.saveAndFlush(client);
		} catch (Exception e) {
			throw new ClientException("An error occurred while saving the client");
		}
	}

	@Override
	public void updateClient(Long id, Client client) throws ClientException {
		try {
			client.setId(clientDAO.findOne(id).getId());
			client.setStatus(statusDAO.findStatusByName(client.getStatus().getName()));
			clientDAO.saveAndFlush(client);
		} catch (Exception e) {
			throw new ClientException("An error occurred while updating the client");
		}
	}

	@Override
	public void deleteClient(Client client) throws ClientException {
		try {
			clientDAO.delete(client);
		} catch (Exception e) {
			throw new ClientException("An error occurred while deleting the client");
		}
	}
}
