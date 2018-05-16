package com.ewp.crm.service.impl;


import com.ewp.crm.exceptions.client.ClientExistsException;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	//TODO упростить
	@Override
    public void addClient(Client client) {
		phoneNumberValidation(client);
		setEmptyNull(client);
		if(client.getEmail()!=null) {
			if(clientRepository.findClientByEmail(client.getEmail())!=null) {
				throw new ClientExistsException();
			}
		}
		if(client.getPhoneNumber()!=null) {
			if(clientRepository.findClientByPhoneNumber(client.getPhoneNumber())!=null) {
				throw new ClientExistsException();
			}
		}
        clientRepository.saveAndFlush(client);
    }

	//TODO упростить
    private void setEmptyNull(Client client) {
	    if(client.getPhoneNumber() !=null && client.getPhoneNumber().isEmpty()){
		    client.setPhoneNumber(null);
	    }
	    if(client.getEmail() !=null && client.getEmail().isEmpty()){
		    client.setEmail(null);
	    }
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

	//TODO упростить
    @Override
    public void updateClient(Client client) {
		phoneNumberValidation(client);
		setEmptyNull(client);
	    if(client.getEmail()!=null) {
		    Client clientByMail = clientRepository.findClientByEmail(client.getEmail());
		    if (clientByMail != null && !clientByMail.getId().equals(client.getId())) {
			    throw new ClientExistsException();
		    }
	    }
	    if(client.getPhoneNumber()!=null){
		    Client clientByPhone =  clientRepository.findClientByPhoneNumber(client.getPhoneNumber());
		    if (clientByPhone != null && !clientByPhone.getId().equals(client.getId())) {
			    throw new ClientExistsException();
		    }
	    }
        clientRepository.saveAndFlush(client);
    }

    private void phoneNumberValidation(Client client) {
		String phoneNumber = client.getPhoneNumber();
	    Pattern pattern = Pattern.compile("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$");
	    Matcher matcher = pattern.matcher(phoneNumber);
	    if (matcher.matches()) {
	    	client.setCanCall(true);
	    	if (phoneNumber.startsWith("8")) {
	    		phoneNumber = phoneNumber.replaceFirst("8", "7");
		    }
		    client.setPhoneNumber(phoneNumber.replaceAll("[+()-]", "")
				    .replaceAll("\\s", ""));
	    } else {
	    	client.setCanCall(false);
	    }
    }
}
