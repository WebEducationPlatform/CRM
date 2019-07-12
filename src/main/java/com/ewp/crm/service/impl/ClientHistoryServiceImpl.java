package com.ewp.crm.service.impl;

import com.ewp.crm.controllers.rest.IPTelephonyRestController;
import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.ClientHistoryDto;
import com.ewp.crm.repository.interfaces.ClientHistoryRepository;
import com.ewp.crm.service.interfaces.AssignSkypeCallService;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.MessageService;
import org.apache.commons.lang3.builder.DiffResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientHistoryServiceImpl implements ClientHistoryService {

	private static Logger logger = LoggerFactory.getLogger(ClientHistoryServiceImpl.class);

	private final ClientHistoryRepository clientHistoryRepository;
	private final MessageService messageService;
	private final AssignSkypeCallService assignSkypeCallService;

	@Autowired
	public ClientHistoryServiceImpl(ClientHistoryRepository clientHistoryRepository,
									MessageService messageService,
									AssignSkypeCallService assignSkypeCallService) {
		this.clientHistoryRepository = clientHistoryRepository;
		this.messageService = messageService;
		this.assignSkypeCallService = assignSkypeCallService;
	}

	@Override
	public Optional<ClientHistory> addHistory(ClientHistory history) {
		return Optional.of(clientHistoryRepository.saveAndFlush(history));
	}

	@Override
	public List<ClientHistory> getByClientId(long id) {
		return clientHistoryRepository.getByClientId(id);
	}

	@Override
	public Optional<ClientHistory> getFirstByClientId(long id) {
		return Optional.ofNullable(clientHistoryRepository.getFirstByClientId(id));
	}

	@Override
	public List<ClientHistory> getAllByClientId(long id, Pageable pageable) {
		return clientHistoryRepository.getAllByClientId(id, pageable).stream().peek((h) -> {
			if (h.getType().equals(ClientHistory.Type.CALL) && (h.getLink() == null || IPTelephonyRestController.INIT_RECORD_LINK.equals(h.getLink()))) {
				h.setTitle(h.getTitle() + ClientHistory.Type.CALL_WITHOUT_RECORD.getInfo());
			}
		}).collect(Collectors.toList());
	}

	@Override
	public List<ClientHistoryDto> getAllDtoByClientId(long id, int page, int pageSize, boolean isAsc) {
		return clientHistoryRepository.getAllDtoByClientId(id, page, pageSize, isAsc);
	}

	@Override
	public Optional<ClientHistory> createHistory(String socialRequest) {
		logger.info("creation of client history...");
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.SOCIAL_REQUEST);
		clientHistory.setTitle(ClientHistory.Type.SOCIAL_REQUEST.getInfo() + " " + socialRequest);
		return Optional.of(clientHistory);
	}

	/*
		worker change status on [status]
		worker add description to client "[description]"
		worker postpone client until [date]
		worker check notification
		worker assigned
		worker unassigned
	*/
	@Override
	public Optional<ClientHistory> createHistory(User user, Client client, ClientHistory.Type type) {
		logger.info("creation of client history...");
		Optional<AssignSkypeCall> assignSkypeCall;
		ClientHistory clientHistory = new ClientHistory(type);
		String action = user.getFullName() + " " + type.getInfo();
		StringBuilder title = new StringBuilder(action);
		switch (type) {
			case DESCRIPTION:
				title.append(" ").append("\"").append(client.getClientDescriptionComment()).append("\"");
				break;
			case POSTPONE:
				title.append(" ");
				title.append("(");
				title.append(ZonedDateTime.parse(client.getPostponeDate().toString()).format(DateTimeFormatter.ofPattern("dd MMM yyyy'г' HH:mm")));
				title.append(")");
				break;
			case REMOVE_POSTPONE:
				title.append(" ");
				break;
			case SKYPE:
			case SKYPE_UPDATE:
			case SKYPE_DELETE:
				assignSkypeCall = assignSkypeCallService.getAssignSkypeCallByClientId(client.getId());
				if (assignSkypeCall.isPresent()) {
					title.append(" ");
					title.append("(");
					title.append(ZonedDateTime.parse(
							assignSkypeCall.get().getSkypeCallDate().toString())
							.withZoneSameInstant(ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ofPattern("dd MMM yyyy'г' HH:mm МСК")));
					title.append(")");
				}
				break;
			case STATUS:
				title.append(" ").append(client.getStatus());
				break;
			case ASSIGN:
			case UNASSIGN:
				title.append("ся");
				break;
		}
		clientHistory.setTitle(title.toString());
		return Optional.of(clientHistory);
	}

	//worker change status on [status] from [last_status]
    @Override
	public Optional<ClientHistory> createHistoryOfChangingStatus(User user, Client client, Status lastStatus) {
        logger.info("creation of client history...");
        ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.STATUS);
        String action = user.getFullName() + " " + ClientHistory.Type.STATUS.getInfo();
        clientHistory.setTitle(action + " " + client.getStatus() + " из " + lastStatus);
        return Optional.of(clientHistory);
    }

	//change status on [status] from [last_status]
    @Override
	public Optional<ClientHistory> createHistoryOfChangingStatus(Client client, Status lastStatus) {
        logger.info("creation of client history...");
        ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.STATUS);
        String action = ClientHistory.Type.STATUS.getInfo();
        clientHistory.setTitle("Автоматически " + action + " " + client.getStatus() + " из " + lastStatus);
        return Optional.of(clientHistory);
    }

	/*
		admin assigned worker to client
		admin unassigned worker on client
	 */
	@Override
	public Optional<ClientHistory> createHistory(User admin, User worker, Client client, ClientHistory.Type type) {
		logger.info("creation of client history...");
		ClientHistory clientHistory = new ClientHistory(type);
		clientHistory.setTitle(admin.getFullName() + " " + type.getInfo() + " " + worker.getFullName());
		return Optional.of(clientHistory);
	}

	// worker call to client [link]
	@Override
	public Optional<ClientHistory> createHistory(User user, Client client, ClientHistory.Type type, String link) {
		logger.info("creation of client history...");
		ClientHistory clientHistory = new ClientHistory(type);
		clientHistory.setLink(link);
		clientHistory.setTitle(user.getFullName() + " " + type.getInfo());
		return Optional.of(clientHistory);
	}

	@Override
	public Optional<ClientHistory> createInfoHistory(User user, Client client, ClientHistory.Type type, String info) {
		logger.info("creation of client info history...");
		ClientHistory clientHistory = new ClientHistory(type);
		clientHistory.setTitle(user.getFullName() + " " + type.getInfo());
		clientHistory.setTitle(user.getFullName() + " " + clientHistory.getType().getInfo());
		if (info != null && !info.isEmpty()) {
			Optional<Message> message = messageService.addMessage(Message.Type.DATA, info);
			if (message.isPresent()) {
				clientHistory.setMessage(message.get());
				clientHistory.setLink(message.get().getId().toString());
			}
		}
		return Optional.of(clientHistory);
	}

	@Override
	public Optional<ClientHistory> createHistory(User user , String recordLink) {
		logger.info("creation of history...");
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.CALL);
		clientHistory.setRecordLink(recordLink);
		clientHistory.setTitle(user.getFullName() + " " + ClientHistory.Type.CALL.getInfo());
		return Optional.of(clientHistory);
	}


	/*
		worker send message by email/vk/facebook/sms [link]
	 */
	@Override
	public Optional<ClientHistory> createHistory(User user, Client client, Message message) {
		logger.info("creation of history...");
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.SEND_MESSAGE);
		clientHistory.setMessage(message);
		clientHistory.setLink(message.getId().toString());
		clientHistory.setTitle(user.getFullName() + " " + clientHistory.getType().getInfo() + " " + message.getType().getInfo());
		return Optional.of(clientHistory);
	}

	/*
		Worker add new client
		Worker change client data [link]
	 */
	@Override
	public Optional<ClientHistory> createHistory(User admin, Client prev, Client current, ClientHistory.Type type) {
		logger.info("creation of history...");
		ClientHistory clientHistory = new ClientHistory(type);

        // if user updated client, which has no changes.
        if (current.equals(prev)) {
            logger.info("Can't find changes");
            return Optional.of(clientHistory);
        }

        clientHistory.setTitle(admin.getFullName() + " " + type.getInfo());

		DiffResult diffs = prev.diff(current);

        StringBuilder content = new StringBuilder();

		diffs.getDiffs().stream().map(
				d -> d.getFieldName() + ": " + d.getLeft() + " -> " + d.getRight())
				.forEach(str -> content.append(str).append("\n"));

		Optional<Message> message = messageService.addMessage(Message.Type.DATA, content.toString());
		if (message.isPresent()) {
			clientHistory.setMessage(message.get());
			clientHistory.setLink(message.get().getId().toString());
		}
		return Optional.of(clientHistory);
	}

	@Override
	public Optional<ClientHistory> createHistoryOfDeletingEmail(User user, Client client, ClientHistory.Type type) {
		logger.info("creation of history...");
		ClientHistory history = new ClientHistory(type);
		history.setTitle(user.getFullName() + " " + type.getInfo());

		Optional<Message> message = messageService.addMessage(Message.Type.DATA, "Email: " + client.getEmail().orElse("not found") + " -> null");
		if (message.isPresent()) {
			history.setMessage(message.get());
			history.setLink(message.get().getId().toString());
		}
		return Optional.of(history);
	}

	@Override
	public Optional<ClientHistory> createHistoryOfDeletingPhone(User user, Client client, ClientHistory.Type type) {
		logger.info("creation of history...");
		ClientHistory history = new ClientHistory(type);
		history.setTitle(user.getFullName() + " " + type.getInfo());

		Optional<Message> message = messageService.addMessage(Message.Type.DATA, "Phone: " + client.getPhoneNumber().orElse("not found") + " -> null");
		if (message.isPresent()) {
			history.setMessage(message.get());
			history.setLink(message.get().getId().toString());
		}
		return Optional.of(history);
	}

	/**
	 * Create client history when student .
	 * @param user change author.
	 * @param type history type.
	 * @return client history object.
	 */
	@Override
	public Optional<ClientHistory> creteStudentHistory(User user, ClientHistory.Type type) {
		logger.debug("creation of become student history...");
		ClientHistory clientHistory = new ClientHistory(type);
		clientHistory.setTitle(user.getFullName() + " " + type.getInfo());
		return Optional.of(clientHistory);
	}

	@Override
	public Optional<ClientHistory> createHistoryFromSlackRegForm(Client prev, Client current, ClientHistory.Type type) {
        ClientHistory clientHistory = new ClientHistory(type);
        clientHistory.setTitle(type.getInfo());
        if (current.equals(prev)) {
            logger.info("Can't find changes");
            return Optional.of(clientHistory);
        }
        StringBuilder content = new StringBuilder();
        DiffResult diffsClients = prev.diffByNameAndLastNameAndEmail(current);
        diffsClients.getDiffs().stream().map(
                d -> d.getFieldName() + ": " + d.getLeft() + " -> " + d.getRight())
                .forEach(str -> content.append(str).append("\n"));
        if (content.toString().isEmpty()) {
            logger.info("Can't find changes");
            return Optional.of(clientHistory);
        }
        Optional<Message> message = messageService.addMessage(Message.Type.DATA, content.toString());
        if (message.isPresent()) {
            clientHistory.setMessage(message.get());
            clientHistory.setLink(message.get().getId().toString());
        }
        return Optional.of(clientHistory);
    }

	/**
	 * Create client history when student modified.
	 * @param user change author.
	 * @param prev previous student object.
	 * @param current current student object.
	 * @param type history type.
	 * @return client history object.
	 */
	@Override
	public Optional<ClientHistory> createStudentUpdateHistory(User user, Student prev, Student current, ClientHistory.Type type) {
		logger.debug("creation of student history...");
		ClientHistory clientHistory = new ClientHistory(type);
		clientHistory.setTitle(user.getFullName() + " " + type.getInfo());
		if (current.equals(prev)) {
			logger.info("Can't find changes");
			return Optional.of(clientHistory);
		}
		DiffResult diffs = prev.diff(current);
		DiffResult diffsClients = prev.getClient().diffByNameAndLastNameAndEmail(current.getClient());
		StringBuilder content = new StringBuilder();
		diffs.getDiffs().stream().map(
				d -> d.getFieldName() + ": " + d.getLeft() + " -> " + d.getRight())
				.forEach(str -> content.append(str).append("\n"));
		diffsClients.getDiffs().stream().map(
				d -> d.getFieldName() + ": " + d.getLeft() + " -> " + d.getRight())
				.forEach(str -> content.append(str).append("\n"));
		if (content.toString().isEmpty()) {
			logger.info("Can't find changes");
			return Optional.of(clientHistory);
		}
		Optional<Message> message = messageService.addMessage(Message.Type.DATA, content.toString());
		if (message.isPresent()) {
			clientHistory.setMessage(message.get());
			clientHistory.setLink(message.get().getId().toString());
		}
		return Optional.of(clientHistory);
	}

}
