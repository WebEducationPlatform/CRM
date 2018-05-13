package com.ewp.crm.service.impl;


import com.ewp.crm.models.Client;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public Client getClientByPhoneNumber(String phoneNumber) {
		return clientRepository.findClientByPhoneNumber(phoneNumber);
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
	public List<Client> getChangeActiveClients() {
		return clientRepository.getChangeActiveClients();
	}

	@Override
	public List<Client> findClientsByManyIds(List<Long> ids) {
		return clientRepository.findByIdIn(ids);
	}

	@Override
	public void updateBatchClients(List<Client> clients) {
		clientRepository.updateBatchClients(clients);
	}

	@Override
    public void addClient(Client client) {
        clientRepository.saveAndFlush(client);
    }

	@Override
	public List<String> getClientsEmails() {
		return clientRepository.getClientsEmail();
	}

	@Override
	public List<String> getClientsPhoneNumbers() {
		return clientRepository.getClientsPhoneNumber();
	}

	@Override
	public List<String> getFilteredClientsEmail(FilteringCondition filteringCondition) {
		return clientRepository.getFilteredClientsEmail(filteringCondition);
	}

	@Override
	public List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition) {
		return clientRepository.getFilteredClientsPhoneNumber(filteringCondition);
	}

	@Override
	public List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition) {
		return clientRepository.getFilteredClientsSNLinks(filteringCondition);
	}

    @Override
    public void updateClient(Client client) {
        clientRepository.saveAndFlush(client);
    }
}
