package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.*;
import com.ewp.crm.models.SortedStatuses.SortingType;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ClientService extends CommonService<Client> {

	List<Client> getAllClientsByStatus(Status status);

	List<Client> getAllClients();

	Client getClientBySkype(String skypeLogin);

	List<Client> getClientsByOwnerUser(User ownerUser);

	Client getClientByEmail(String name);

	Client getClientByPhoneNumber(String phoneNumber);

	Client getClientByID(Long id);

	Client getClientBySocialProfile(SocialProfile socialProfile);

	void addClient(Client client);

	void updateClient(Client client);

	List<Client> filteringClient(FilteringCondition filteringCondition);

	List<Client> getChangeActiveClients();

	List<Client> getClientsByManyIds(List<Long> ids);

	void updateBatchClients(List<Client> clients);

	void addBatchClients(List<Client> clients);

	List<String> getClientsEmails();

	List<String> getClientsPhoneNumbers();

	List<String> getFilteredClientsEmail(FilteringCondition filteringCondition);

	List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition);

	List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition);

	List<Client> getClientsByStatusAndOwnerUserOrOwnerUserIsNull(Status status, User ownUser);

	List<Client> getAllClientsByPage(Pageable pageable);

	List<Client> getClientsBySearchPhrase(String search);

	List<Client> getOrderedClientsInStatus(Status status, SortingType order);
}
