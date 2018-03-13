package com.ewp.crm.service.impl;


import com.ewp.crm.exceptions.client.ClientExistsException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

	private final ClientRepository clientRepository;

	@Autowired
	public ClientServiceImpl(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	@Override
	public List<Client> getAllClients() {
		return clientRepository.findAll();
	}

	@Override
	public List<Client> getClientsByOwnerUser(User ownerUser) {
		return clientRepository.getClientsByOwnerUser(ownerUser);
	}

	@Override
	public Client getClientByEmail(String email) {
		return clientRepository.findClientByEmail(email);
	}

	@Override
	public Client getClientByID(Long id) {
		return clientRepository.findOne(id);
	}

	@Override
	public void deleteClient(Long id) {
		clientRepository.delete(id);
	}

	@Override
	public void deleteClient(Client client) {
		clientRepository.delete(client);
	}

	@Override
	public List<Client> filteringClient(FilteringCondition filteringCondition) {
		return clientRepository.filteringClient(filteringCondition);
	}

	@Override
	public void addClient(Client client) {
		checkNewClient(client);
		clientRepository.saveAndFlush(client);
	}

	@Override
	public void updateClient(Client client) {
		checkExistClient(client);
		clientRepository.saveAndFlush(client);
	}


	private void checkNewClient(Client client) {
		if ((clientRepository.findClientByEmail(client.getEmail()) != null)
				|| (clientRepository.findClientByPhoneNumber(client.getPhoneNumber()) != null)) {
			throw new ClientExistsException("Клиент уже существует");
		}
	}

	private void checkExistClient(Client client) {
		Client currentClientByEmail;
		Client currentClientByPhone;
		if (((currentClientByEmail = clientRepository.findClientByEmail(client.getEmail())) != null && !currentClientByEmail.getId().equals(client.getId()))
				|| ((currentClientByPhone = clientRepository.findClientByPhoneNumber(client.getPhoneNumber())) != null && !currentClientByPhone.getId().equals(client.getId()))) {
			throw new ClientExistsException("Клиент уже существует");
		}
	}
}
