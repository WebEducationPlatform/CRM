package com.ewp.crm.controllers.rest.api;

import com.ewp.crm.controllers.rest.admin.AdminRestClientController;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/rest/api/client")
public class APIRestClientController {
	private static Logger logger = LoggerFactory.getLogger(AdminRestClientController.class);
	@Autowired
	ClientService clientService;
	@Autowired
	StatusService statusService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAll() {
		return ResponseEntity.ok(clientService.getAll());
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Client> getClientByID(@PathVariable Long id) {
		return ResponseEntity.ok(clientService.get(id));
	}

	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity addClient(@RequestBody Client client) {
		clientService.add(client);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	
	@PutMapping(value = "/update")
	public ResponseEntity updateClient(@RequestBody Client currentClient) {
		Client clientFromDB = clientService.get(currentClient.getId());
		currentClient.setWhatsappMessages(clientFromDB.getWhatsappMessages());
		currentClient.setHistory(clientFromDB.getHistory());
		currentClient.setComments(clientFromDB.getComments());
		currentClient.setOwnerUser(clientFromDB.getOwnerUser());
		currentClient.setStatus(clientFromDB.getStatus());
		currentClient.setStudent(clientFromDB.getStudent());
		if (clientFromDB.getDateOfRegistration() == null) {
			clientService.setClientDateOfRegistrationByHistoryDate(currentClient);
		} else {
			currentClient.setDateOfRegistration(ZonedDateTime.parse(clientFromDB.getDateOfRegistration().toString()));
		}
		currentClient.setSmsInfo(clientFromDB.getSmsInfo());
		currentClient.setCanCall(clientFromDB.isCanCall());
		currentClient.setCallRecords(clientFromDB.getCallRecords());
		currentClient.setClientDescriptionComment(clientFromDB.getClientDescriptionComment());
		currentClient.setLiveSkypeCall(clientFromDB.isLiveSkypeCall());
		currentClient.setState(clientFromDB.getState());
		if (currentClient.equals(clientFromDB)) {
			return ResponseEntity.noContent().build();
		}
//        clientHistoryService.createHistory(userFromSession, clientFromDB, currentClient, ClientHistory.Type.UPDATE).ifPresent(currentClient::addHistory);

		// Код ниже необходим чтобы задедектить изменение сущностей которые смапленны аннотацией @ElementCollection
		// Относится к списку почты и телефона, ошибка заключается в том что когда пытаешься изменить порядок уже существующих данных
		// Происходит ошибка уникальности (неправильны мердж сущности) в остальном всё ок
		// Если произошёл такой случай то руками удаляем зависимости, сохраняем и записываем что пришло
		List<String> emails = currentClient.getClientEmails();
		List<String> phones = currentClient.getClientPhones();

		List<String> emailsFromDb = clientFromDB.getClientEmails();
		List<String> phonesFromDb = clientFromDB.getClientPhones();

		boolean needUpdateClient = false;

		// Если размеры равны начинаем проверку
		int count = Math.min(emails.size(), emailsFromDb.size());
		for (int i = 0; i < count; i++) {
			// Если почты не равны взводим флаг что нужн доп апдейт клиента
			if (!emails.get(i).equals(emailsFromDb.get(i))) {
				emailsFromDb.clear();
				needUpdateClient = true;
				break;
			}
		}

		// Если флаг взведён даже не проверям телефоны
		if (!needUpdateClient) {
			count = Math.min(phones.size(), phonesFromDb.size());
			for (int i = 0; i < count; i++) {
				// Если почты не равны взводим флаг что нужн доп апдейт клиента
				if (!phones.get(i).equals(phonesFromDb.get(i))) {
					phonesFromDb.clear();
					needUpdateClient = true;
					break;
				}
			}
		}

		// Проверяем достаточные условия для удаления/записи
		if (needUpdateClient) {
			clientService.updateClient(clientFromDB);
		}

		clientService.updateClient(currentClient);
        logger.info("{} has updated client: id {}, email {}", userFromSession.getFullName(), currentClient.getId(), currentClient.getEmail().orElse("not found"));
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PutMapping(value = "/updatestatus/{statusId}")
	public ResponseEntity updateClientStatus(@RequestBody Client currentClient,
											 @PathVariable Long statusId) {
		Client clientFromDB = clientService.get(currentClient.getId());
		Status newStatus = statusService.get(statusId).orElse(null);
		clientFromDB.setStatus(newStatus);
		clientService.updateClient(clientFromDB);

		return ResponseEntity.ok(HttpStatus.OK);

	}


	@DeleteMapping("/{clientId}")
	public ResponseEntity removeClient(@PathVariable(name = "clientId") Long clientId) {

		Client clientFromDB = clientService.get(clientId);
		if (Objects.isNull(clientFromDB)) {
			return ResponseEntity.notFound().build();
		}
		clientService.delete(clientId);

 logger.info("{} has delete client: id {}, email {}", userFromSession.getFullName(), clientFromDB.getId(), clientFromDB.getEmail().orElse("not found"));
		return ResponseEntity.ok(HttpStatus.OK);
	}

}
