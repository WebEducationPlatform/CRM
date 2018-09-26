package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientHistoryService {

	ClientHistory addHistory(ClientHistory history);

	ClientHistory createHistory(String socialRequest);

	ClientHistory createHistory(User user, Client client, ClientHistory.Type type);

	ClientHistory createHistory(User admin, User worker, Client client, ClientHistory.Type type);

	ClientHistory createHistory(User user, Client client, ClientHistory.Type type, String link);

	ClientHistory createInfoHistory(User user, Client client, ClientHistory.Type type, String info);

	ClientHistory createHistory(User user, String recordLink);

	ClientHistory createHistory(User user, Client client, Message message);

	ClientHistory createHistory(User admin, Client prev, Client current, ClientHistory.Type type);

	ClientHistory creteStudentAddHistory(User user, Student student, ClientHistory.Type type);

	ClientHistory createStudentUpdateHistory(User user, Student prev, Student current, ClientHistory.Type type);

	List<ClientHistory> getClientById(long id);

	List<ClientHistory> getAllClientById(long id, Pageable pageable);
}
