package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.CallRecord;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.dto.ClientHistoryDto;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.CallRecordService;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/rest/api/client/history")
public class ClientHistoryRestController {

	private static final Logger logger = LoggerFactory.getLogger(ClientHistoryRestController.class);

	private final ClientHistoryService clientHistoryService;

	@Value("10")
	private int pageSize;

	@Autowired
	public ClientHistoryRestController(ClientHistoryService clientHistoryService) {
		this.clientHistoryService = clientHistoryService;
	}

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	CallRecordService callRecordService;

	@GetMapping("/rest/getHistory/{clientId}")
	public ResponseEntity getClientHistory(@PathVariable("clientId") long id, @RequestParam("page")int page, @RequestParam("isAsc")boolean isAsc) {
		List<ClientHistoryDto> clientHistory = clientHistoryService.getAllDtoByClientId(id, page, pageSize, isAsc);
		return ResponseEntity.ok(clientHistory);
	}

	//Добавление записи по id клиента
	@PostMapping("/rest/addHistory/{clientId}")
	public ResponseEntity addClientHistory(@PathVariable("clientId") long id, @RequestBody ClientHistory clientHistory) {
		clientHistory.setClient(clientRepository.getClientById(id));
		Optional<ClientHistory> result = clientHistoryService.addHistory(clientHistory);
		return ResponseEntity.ok(result);
	}

	//Удаление записи по id истории
	@DeleteMapping("/rest/deleteHistory/{clientHistoryId}")
	public ResponseEntity deleteClientHistory(@PathVariable("clientHistoryId") long clientHistoryId) {
		//Проверяем есть ли связанная с ClientHistory запись callRecord и если есть удаляем ее.
		Optional<CallRecord> callRecord = callRecordService.getByClientHistory_Id(clientHistoryId);
		if (callRecord.isPresent()) {
			callRecordService.delete(callRecord.get());
		}
		//Удаляем запись ClientHistory по Id
		clientHistoryService.deleteClientHistoryById(clientHistoryId);
		return new ResponseEntity(OK);
	}

	//Обновление записи по id клиента
	@PutMapping("/rest/updateHistory/{clientId}")
	public ResponseEntity updateClientHistory(@PathVariable("clientId") long id, @RequestBody ClientHistory clientHistory) {
		clientHistory.setClient(clientRepository.getClientById(id));
		Optional<ClientHistory> result = clientHistoryService.addHistory(clientHistory);
		return ResponseEntity.ok(result);
	}
}
