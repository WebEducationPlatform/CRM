package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.*;
import com.ewp.crm.models.SortedStatuses.SortingType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface ClientService extends CommonService<Client> {

	List<String> getSocialIdsForStudentsByStatusAndSocialProfileType(List<Status> statuses, String socialProfileType);

	List<String> getSocialIdsForStudentsBySocialProfileType(String socialProfileType);

	List<Client> getAllClientsByStatus(Status status);

	List<Client> getAllClients();

	Optional<Client> getClientBySkype(String skypeLogin);

	List<Client> getClientsByOwnerUser(User ownerUser);

	Optional<Client> getClientByEmail(String name);

	Optional<Client> getClientByPhoneNumber(String phoneNumber);

	Optional<Client> getClientByClientPhonesLike(String phoneNumber);

    Optional<Client> getClientByClientPhonesEquals(String phoneNumber);

    Optional<Client> getClientByClientPhonesIn(String phoneNumber);

	Optional<Client> getClientByClientEmailsEquals(String email);

	Optional<Client> getClientByID(Long id);

	Optional<Client> getClientBySocialProfile(SocialProfile socialProfile);

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

	List<Client> getAllClientsByPage(Pageable pageable);

	List<Client> getClientsBySearchPhrase(String search);

	List<Client> getOrderedClientsInStatus(Status status, SortingType order, User user);

	Optional<Client> findByNameAndLastNameIgnoreCase(String name, String lastName);
  
	void updateClientFromContractForm(Client client, ContractDataForm contractForm, User authUser);

	void setContractLink(Long clientId, String contractLink);
}