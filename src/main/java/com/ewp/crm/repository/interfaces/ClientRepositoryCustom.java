package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.*;
import com.ewp.crm.models.SortedStatuses.SortingType;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;

public interface ClientRepositoryCustom {

    boolean hasClientSocialProfileByType(Client client, String socialProfileType);

	String getSlackLinkHashForClient(Client client);

    ClientHistory getNearestClientHistoryBeforeDate(Client client, ZonedDateTime dateTime, List<ClientHistory.Type> types);

    ClientHistory getNearestClientHistoryAfterDate(Client client, ZonedDateTime dateTime, List<ClientHistory.Type> types);

	ClientHistory getNearestClientHistoryAfterDateByHistoryType(Client client, ZonedDateTime dateTime, List<ClientHistory.Type> types, String title);

	ClientHistory getHistoryByClientAndHistoryTimeIntervalAndHistoryType(Client client, ZonedDateTime firstDay, ZonedDateTime lastDay, List<ClientHistory.Type> types, String title);

    boolean hasClientBeenInStatusBefore(long clientId, ZonedDateTime date, String statusName);

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

	List<Client> getChangedStatusClientsInPeriod(ZonedDateTime firstDate, ZonedDateTime lastDate, List<ClientHistory.Type> types, List<Status> excludeStatuses, String title);

	List<Client> getClientByHistoryTimeIntervalAndHistoryType(ZonedDateTime firstDay, ZonedDateTime lastDay, List<ClientHistory.Type> types, List<Status> excludeStatuses);

	long getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(ZonedDateTime firstDay, ZonedDateTime lastDay, List<ClientHistory.Type> types, String title);

	Long countByDate(String date);

	List<Client> getClientsBySearchPhrase(String search);

	List<Client> getClientsInStatusOrderedByRegistration(Status status, SortingType order, boolean isAdmin, User user);

	boolean isTelegramClientPresent(Integer id);

	Client getClientBySocialProfile(String id, String socialProfileType);

	List<Client> getClientsInStatusOrderedByHistory(Status status, SortingType order, boolean isAdmin, User user);

	void transferClientsBetweenOwners(User sender, User receiver);

	List<Long> getClientsIdInStatusOrderedByHistory(Long idStatus, String order, boolean isAdmin, User user);

	List<Long> getClientsIdFromStatus(Long statusId);

	String getClientNameById(Long id);

	String getClientLastNameById(Long id);

	boolean getClientHideById(Long id);

	BigInteger getClientOwnerUserIdById(Long id);

	BigInteger getClientOwnerMentorIdById(Long id);

	List<Long> getClientsIdInStatusOrderedByRegistration(Long statusId, String order, boolean isAdmin, User user);
}