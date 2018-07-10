package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ClientService extends CommonService<Client> {

	List<Client> getClientsByOwnerUser(User ownerUser);

	Client getClientByEmail(String name);

	Client getClientByPhoneNumber(String phoneNumber);

	void addClient(Client client);

	void updateClient(Client client);

	List<Client> filteringClient(FilteringCondition filteringCondition);

	List<Client> getChangeActiveClients();

	List<Client> findClientsByManyIds(List<Long> ids);

	void updateBatchClients(List<Client> clients);

	void addBatchClients(List<Client> clients);

	List<String> getClientsEmails();

	List<String> getClientsPhoneNumbers();

	List<String> getFilteredClientsEmail(FilteringCondition filteringCondition);

	List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition);

	List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition);

	List<Client> findByStatusAndOwnerUserOrOwnerUserIsNull(Status status, User ownUser);

	List<Client> findAllByPage(Pageable pageable);

	List<Client> findAllByOwnerUser(Pageable pageable, User clientOwner);
}