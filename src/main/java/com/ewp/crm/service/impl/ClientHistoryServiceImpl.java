package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Message;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.ClientHistoryRepository;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.MessageService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;

@Service
public class ClientHistoryServiceImpl implements ClientHistoryService {

	private final ClientHistoryRepository clientHistoryRepository;

	private static Logger logger = LoggerFactory.getLogger(ClientHistoryServiceImpl.class);
	private final MessageService messageService;

	@Autowired
	public ClientHistoryServiceImpl(ClientHistoryRepository clientHistoryRepository, MessageService messageService) {
		this.clientHistoryRepository = clientHistoryRepository;
		this.messageService = messageService;
	}

	@Override
	public ClientHistory addHistory(ClientHistory history) {
		return clientHistoryRepository.saveAndFlush(history);
	}

	@Override
	public ClientHistory createHistory(String socialRequest) {
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.SOCIAL_REQUEST);
		clientHistory.setTitle(ClientHistory.Type.SOCIAL_REQUEST.getInfo() + " " + socialRequest);
		return clientHistory;
	}

	//работник изменил статус на [статус]
	//работник добавил description клиенту [описание]
	//работник установил напоминание на [дата]
	//работник прочитал нотификацию клиента
	//работник прикрепился
	//работник открепился
	@Override
	public ClientHistory createHistory(User user, Client client, ClientHistory.Type type) {
		ClientHistory clientHistory = new ClientHistory(type);
		String action = user.getFullName() + " " + type.getInfo();
		StringBuilder title = new StringBuilder(action);
		switch (type) {
			case DESCRIPTION:
				title.append(" ").append("\"").append(client.getClientDescriptionComment()).append("\"");
				break;
			case POSTPONE:
				title.append(" ").append(new DateTime(client.getPostponeDate()).toString("dd MMM 'в' HH:mm yyyy'г'"));
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
		return clientHistory;
	}

	/*
		Работник прикрепил Работника
		Работник открепил Работника
		Работник прикрепился
		Работник открепился
	 */
	@Override
	public ClientHistory createHistory(User admin, User worker, Client client, ClientHistory.Type type) {
		ClientHistory clientHistory = new ClientHistory(type);
		clientHistory.setTitle(admin.getFullName() + " " + type.getInfo() + " " + worker.getFullName());
		return clientHistory;
	}

	// Работник совершил звонок
	@Override
	public ClientHistory createHistory(User user, Client client, ClientHistory.Type type, String link) {
		ClientHistory clientHistory = new ClientHistory(type);
		clientHistory.setLink(link);
		clientHistory.setTitle(user.getFullName() + " " + type.getInfo());
		return clientHistory;
	}

	/*
	Работник отправил сообщение по sms link
	Работник отправил сообщение по vk link
	Работник отправил сообщение по facebook link
	Работник отправил сообщение по email link
	 */
	@Override
	public ClientHistory createHistory(User user, Client client, Message message) {
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.SEND_MESSAGE);
		clientHistory.setMessage(message);
		clientHistory.setLink("/client/message/info/" + message.getId());
		clientHistory.setTitle(user.getFullName() + " " + clientHistory.getType().getInfo() + " " + message.getType().getInfo());
		return clientHistory;
	}

	@Override
	public ClientHistory createHistory(User admin, Client prev, Client current, ClientHistory.Type type) {
		ClientHistory clientHistory = new ClientHistory(type);
		clientHistory.setTitle(admin.getFullName() + " " + type.getInfo());
		if (prev.equals(current)) {
			return clientHistory;
		}
		try {
			String buildChanges = parseChangesToHtml(findChanges(prev, current));
			Message message = messageService.addMessage(Message.Type.DATA, buildChanges);
			clientHistory.setMessage(message);
			clientHistory.setLink("/client/message/info/" + message.getId());
		} catch (IllegalAccessException e) {
			logger.error("Reflection exception: Cant build client changes with id: {}", prev.getId());
		}
		return clientHistory;
	}


	//TODO сделать лучше
	private String findChanges(Client prev, Client current) throws IllegalAccessException {
		StringBuilder changesBuild = new StringBuilder();
		Field[] prevFields = prev.getClass().getDeclaredFields();
		Field[] currentFields = current.getClass().getDeclaredFields();

		for (int i = 0; i < prevFields.length; i++) {
			Field prevField = prevFields[i];
			Field currentField = currentFields[i];
			prevField.setAccessible(true);
			currentField.setAccessible(true);
			Object data1 = prevField.get(prev);
			Object data2 = prevField.get(current);
			if (data1 != null && data2 != null) {
				if (data1 instanceof Collection) {
					iterateCollection(data1, data2, changesBuild);
				} else if (!data1.equals(data2)) {
					changesBuild.append(getRow(data1, data2));
				}
			}
		}
		return changesBuild.toString();
	}

	private void iterateCollection(Object data1, Object data2, StringBuilder changesBuild) {
		Collection<?> collection1 = ((Collection<?>) data1);
		Collection<?> collection2 = ((Collection<?>) data2);
		Iterator<?> iterator1 = collection1.iterator();
		Iterator<?> iterator2 = collection2.iterator();
		while (iterator1.hasNext() && iterator2.hasNext()) {
			Object element1 = iterator1.next();
			Object element2 = iterator2.next();
			if(!element1.equals(element2)) {
				changesBuild.append(getRow(element1,element2));
			}
		}
		while (iterator1.hasNext()) {
			changesBuild.append("{prev}");
			changesBuild.append(iterator1.next());
			changesBuild.append("{close}");
			changesBuild.append(" удалено");
			changesBuild.append("{br}");
		}
		while (iterator2.hasNext()) {
			changesBuild.append("{current}");
			changesBuild.append(iterator2.next());
			changesBuild.append("{close}");
			changesBuild.append(" добавлено");
			changesBuild.append("{br}");
		}
	}

	private String getRow(Object obj1, Object obj2) {
		return "{prev}" + obj1 + "{close} изменено на {current}" + obj2 + "{close}{br}";
	}

	private String parseChangesToHtml(String changes) {
		return changes.replaceAll("\\{prev}", "<span style=\"background-color:#DCDCDC\">")
				.replaceAll("\\{current}","<span style=\"background-color:#90EE90\">")
				.replaceAll("\\{close}","</span>")
				.replaceAll("\\{br}","<br>");
	}

	private String parseChanges(String changes) {
		return changes.replaceAll("\\{prev}", "")
				.replaceAll("\\{current}","")
				.replaceAll("\\{close}","")
				.replaceAll("\\{br}","\n");
	}
}
