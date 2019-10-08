package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.ContractDataForm;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SortedStatuses.SortingType;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.ClientDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClientService extends CommonService<Client> {
    boolean hasClientSocialProfileByType(Client client, String socialProfileType);

    boolean inviteToSlack(Client client, String name, String lastName, String email);

    Optional<Client> getClientBySlackInviteHash(String hash);

	Optional<String> generateSlackInviteLink(Long clientId);

	List<String> getSocialIdsForStudentsByStatusAndSocialProfileType(List<Status> statuses, String socialProfileType);

	List<String> getSocialIdsForStudentsBySocialProfileType(String socialProfileType);

	List<Client> getAllClientsByStatus(Status status);

	List<Client> getAllClients();

	Optional<Client> getClientBySkype(String skypeLogin);

	List<Client> getClientsByOwnerUser(User ownerUser);

	Optional<Client> getClientByEmail(String name);

	Optional<Client> getClientByPhoneNumber(String phoneNumber);

	Optional<Client> getClientByID(Long id);

	Optional<Client> getClientBySocialProfile(SocialProfile socialProfile);

	void addClient(Client client, User user);

	void createClientStatusChangingHistory(Status lastStatus, Status newStatus, Client client, boolean clientCreation, User user);

	void updateClient(Client client);

	List<Client> filteringClient(FilteringCondition filteringCondition);

	List<Client> getChangeActiveClients();

	List<Client> getClientsByManyIds(List<Long> ids);

	void updateBatchClients(List<Client> clients);

	void addBatchClients(List<Client> clients);

	List<String> getClientsEmails();

	List<String> getClientsPhoneNumbers();

	List<String> getFilteredClientsEmail(FilteringCondition filteringCondition);

	Optional<List<String>> getClientsEmailsByStatusesIds(List<Long> statusesIds);

	List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition);

	Optional<List<String>> getClientsPhoneNumbersByStatusesIds(List<Long> statusesIds);

	List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition);

	void setClientDateOfRegistrationByHistoryDate(Client client);

	List<Client> getAllClientsByPage(Pageable pageable);

	List<Client> getClientsBySearchPhrase(String search);

	List<Client> getOrderedClientsInStatus(Status status, SortingType order);

	Optional<Client> findByNameAndLastNameIgnoreCase(String name, String lastName);
  
	void updateClientFromContractForm(Client client, ContractDataForm contractForm, User authUser);

	void setContractLink(Long clientId, String contractLink, String contractName);

	List<Client> getAllClientsSortingByLastChange();

	List<Client> getFilteringAndSortClients(FilteringCondition filteringCondition, String sortColumn);

	Optional<Comment> getLastComment(Client client);

	Optional<ClientHistory> getLastHistory(Client client);

	void transferClientsBetweenOwners(User sender, User receiver);

	void transferContractSettingsBetweenUsers(User sender, User receiver);

	void transferClientsBetweenMentors(User sender, User receiver);

	void setOtherInformationLink(Long clientId, String hash);

  List<Client> getSortedClientsByStatus(Status status, SortingType sortingType);

	Optional<List<Client>> getClientsByEmails(List<String> emails);

    List<ClientDto.ClientTransformer> getClientsDtoByEmails(List<String> emails);

	List<String> getClientsCountries();

	List<String> getClientsCities();

	List<Client> getClientsByStatusIsLike(Status status);
}