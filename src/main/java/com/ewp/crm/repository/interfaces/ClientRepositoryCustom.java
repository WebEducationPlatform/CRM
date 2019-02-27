package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.*;
import com.ewp.crm.models.SortedStatuses.SortingType;
import java.time.ZonedDateTime;
import java.util.List;

public interface ClientRepositoryCustom {

	List filteringClient(FilteringCondition filteringCondition);

	List<Client> getChangeActiveClients();

	void updateBatchClients(List<Client> clients);

	void addBatchClients(List<Client> clients);

	List getClientsEmail();

	List<String> getClientsPhoneNumber();

	List<String> getFilteredClientsEmail(FilteringCondition filteringCondition);

	List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition);

	List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition);

	List<Client> getByStatusAndOwnerUserOrOwnerUserIsNull(Status status, User ownUser);

	List<Client> getByStatusAndOwnerUserOrOwnerUserIsNullOrderedByRegistration(Status status, User ownUser, SortingType order);

	List<Client> getByStatusAndOwnerUserOrOwnerUserIsNullOrderedByHistory(Status status, User ownUser, SortingType order);

	List<ClientHistory> getClientByTimeInterval(int days);

	List<Client> getClientByHistoryTimeIntervalAndHistoryType(ZonedDateTime firstDay, ZonedDateTime lastDay, ClientHistory.Type[] types);

	long getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(ZonedDateTime firstDay, ZonedDateTime lastDay, ClientHistory.Type[] types, String title);

	Long countByDate(String date);

	List<Client> getClientsBySearchPhrase(String search);

	List<Client> getClientsInStatusOrderedByRegistration(Status status, SortingType order);

	boolean isTelegramClientPresent(Integer id);

	Client getClientBySocialProfile(String id, String socialProfileType);

	List<Client> getClientsInStatusOrderedByHistory(Status status, SortingType order);

}
