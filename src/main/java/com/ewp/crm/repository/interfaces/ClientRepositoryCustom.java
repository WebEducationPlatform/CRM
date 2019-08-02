package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.*;
import com.ewp.crm.models.SortedStatuses.SortingType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public interface ClientRepositoryCustom {

    boolean hasClientSocialProfileByType(Client client, String socialProfileType);

	String getSlackLinkHashForClient(Client client);

    ClientHistory getNearestClientHistoryBeforeDate(Client client, ZonedDateTime dateTime, List<ClientHistory.Type> types);

    ClientHistory getNearestClientHistoryAfterDate(Client client, ZonedDateTime dateTime, List<ClientHistory.Type> types);

	ClientHistory getNearestClientHistoryAfterDateByHistoryType(Client client, ZonedDateTime dateTime, List<ClientHistory.Type> types, String title);

	ClientHistory getHistoryByClientAndHistoryTimeIntervalAndHistoryType(Client client, ZonedDateTime firstDay, ZonedDateTime lastDay, List<ClientHistory.Type> types, String title);

    boolean hasClientBeenInStatusBefore(long clientId, ZonedDateTime date, String statusName);

	ClientHistory getClientFirstStatusChangingHistory(long clientId);

	boolean hasClientStatusChangingHistory(long clientId);

	List<String> getSocialIdsBySocialProfileTypeAndStatusAndStudentExists(List<Status> statuses, String socialProfileType);

	List<String> getSocialIdsBySocialProfileTypeAndStudentExists(String socialProfileType);

	List filteringClient(FilteringCondition filteringCondition);

	List<Client> filteringClientWithoutPaginator(FilteringCondition filteringCondition);

	List<Client> getChangeActiveClients();

	void updateBatchClients(List<Client> clients);

	void addBatchClients(List<Client> clients);

	List getClientsEmail();

	List<String> getClientsPhoneNumber();

	List<String> getFilteredClientsEmail(FilteringCondition filteringCondition);

	List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition);

	List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition);

	List<ClientHistory> getClientByTimeInterval(int days);

    List<ClientHistory> getAllHistoriesByClientStatusChanging(Client client, List<Status> statuses, List<ClientHistory.Type> types);

	List<ClientHistory> getAllHistoriesByClientAndHistoryType(Client client, List<ClientHistory.Type> types);

	boolean hasClientChangedStatusFromThisToAnotherInPeriod(ZonedDateTime firstDate, ZonedDateTime lastDate, List<ClientHistory.Type> types, List<Status> excludeStatuses, String title);

	Map<Client, List<ClientHistory>> getChangedStatusClientsInPeriod(ZonedDateTime firstDate, ZonedDateTime lastDate, List<ClientHistory.Type> types, List<Status> excludeStatuses, String title);

	List<Client> getClientByHistoryTimeIntervalAndHistoryType(ZonedDateTime firstDay, ZonedDateTime lastDay, List<ClientHistory.Type> types, List<Status> excludeStatuses);

	long getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(ZonedDateTime firstDay, ZonedDateTime lastDay, List<ClientHistory.Type> types, String title);

	Long countByDate(String date);

	List<Client> getClientsBySearchPhrase(String search);

	List<Client> getClientsInStatusOrderedByRegistration(Status status, SortingType order);

	boolean isTelegramClientPresent(Integer id);

	Client getClientBySocialProfile(String id, String socialProfileType);

	List<Client> getClientsInStatusOrderedByHistory(Status status, SortingType order);

	void transferClientsBetweenOwners(User sender, User receiver);
}
