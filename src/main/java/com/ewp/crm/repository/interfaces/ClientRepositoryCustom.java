package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.*;

import java.util.Date;
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

	List<Client> findByStatusAndOwnerUserOrOwnerUserIsNull(Status status, User ownUser);

	List<ClientHistory> getClientByTimeInterval2(int days);

	List<Client> getClientByTimeInterval(int days);

	Long countByDate(String date);
}
