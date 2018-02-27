package com.ewp.crm.service.impl;


import com.ewp.crm.exceptions.client.ClientException;
import com.ewp.crm.models.Client;
import com.ewp.crm.repository.interfaces.ClientDAO;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

	@Autowired
	private ClientDAO clientDAO;

	@Override
	public List<Client> getAllClients() {
		return clientDAO.findAll();
	}

	@Override
	public Client getClientByEmail(String email) {
		return clientDAO.findClientByEmail(email);
	}

	@Override
	public Client getClientByID(Long id) {
		return clientDAO.findOne(id);
	}

	@Override
	public void deleteClient(Long id) {
		clientDAO.delete(id);
	}

	@Override
	public void deleteClient(Client client) {
		clientDAO.delete(client);
	}

	@Override
	public void addClient(Client client) {
		checkNewClient(client);
		clientDAO.saveAndFlush(client);
	}

	@Override
	public void updateClient(Client client) {
		checkExistClient(client);
		clientDAO.saveAndFlush(client);
	}

	private void checkNewClient(Client client) {
		if ((clientDAO.findClientByEmail(client.getEmail()) != null)
				|| (clientDAO.findClientByPhoneNumber(client.getPhoneNumber()) != null)) {
			throw new ClientException("Клиент уже существует");
		}
	}

	private void checkExistClient(Client client) {
		Client currentClientByEmail;
		Client currentClientByPhone;
		if (((currentClientByEmail = clientDAO.findClientByEmail(client.getEmail())) != null && !currentClientByEmail.getId().equals(client.getId()))
				|| ((currentClientByPhone = clientDAO.findClientByPhoneNumber(client.getPhoneNumber())) != null && !currentClientByPhone.getId().equals(client.getId()))) {
			throw new ClientException("Клиент уже существует");
		}
	}
}
