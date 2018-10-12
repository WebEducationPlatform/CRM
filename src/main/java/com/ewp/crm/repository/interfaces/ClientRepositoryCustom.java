package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.*;
import java.time.LocalDateTime;
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

	List<ClientHistory> getClientByTimeInterval(int days);

	List<Client> getClientByHistoryTimeIntervalAndHistoryType(LocalDateTime firstDay, LocalDateTime lastDay, ClientHistory.Type[] types);

	long getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(LocalDateTime firstDay, LocalDateTime lastDay, ClientHistory.Type[] types, String title);

	Long countByDate(String date);

	List<Client> getClientsBySearchWord(String search);
}
