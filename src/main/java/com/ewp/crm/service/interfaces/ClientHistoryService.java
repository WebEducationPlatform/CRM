package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClientHistoryService {

	Optional<ClientHistory> addHistory(ClientHistory history);

	Optional<ClientHistory> createHistory(String socialRequest);

	Optional<ClientHistory> createHistory(User user, Client client, ClientHistory.Type type);

	Optional<ClientHistory> createHistoryOfChangingStatus(User user, Client client, Status lastStatus);

	Optional<ClientHistory> createHistory(User admin, User worker, Client client, ClientHistory.Type type);

	Optional<ClientHistory> createHistory(User user, Client client, ClientHistory.Type type, String link);

	Optional<ClientHistory> createInfoHistory(User user, Client client, ClientHistory.Type type, String info);

	Optional<ClientHistory> createHistory(User user, String recordLink);

	Optional<ClientHistory> createHistory(User user, Client client, Message message);

	Optional<ClientHistory> createHistory(User admin, Client prev, Client current, ClientHistory.Type type);

	Optional<ClientHistory> creteStudentHistory(User user, ClientHistory.Type type);

	Optional<ClientHistory> createHistoryFromSlackRegForm(Client prev, Client current, ClientHistory.Type type);

	Optional<ClientHistory> createStudentUpdateHistory(User user, Student prev, Student current, ClientHistory.Type type);

	Optional<ClientHistory> createHistoryOfDeletingEmail(User user, Client client, ClientHistory.Type type);

	Optional<ClientHistory> createHistoryOfDeletingPhone(User user, Client client, ClientHistory.Type type);

	List<ClientHistory> getByClientId(long id);

	Optional<ClientHistory> getFirstByClientId(long id);

	List<ClientHistory> getAllByClientId(long id, Pageable pageable);
}
