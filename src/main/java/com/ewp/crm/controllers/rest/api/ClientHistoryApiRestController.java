package com.ewp.crm.controllers.rest.api;

import com.ewp.crm.models.CallRecord;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.CallRecordService;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@ComponentScan(basePackages = "com.ewp.crm")
@RequestMapping("/rest/api/client/history")
public class ClientHistoryApiRestController {

    private final ClientHistoryService clientHistoryService;
    private final CallRecordService callRecordService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    public ClientHistoryApiRestController(ClientHistoryService clientHistoryService, CallRecordService callRecordService) {
        this.clientHistoryService = clientHistoryService;
        this.callRecordService = callRecordService;
    }

    //Добавление записи по id клиента
    @PostMapping("/add/{clientId}")
    public ResponseEntity addClientHistory(@PathVariable("clientId") long id, @RequestBody ClientHistory clientHistory) {
        clientHistory.setClient(clientRepository.getClientById(id));
        Optional<ClientHistory> result = clientHistoryService.addHistory(clientHistory);
        return ResponseEntity.ok(result);
    }

    //Удаление записи по id истории
    @PostMapping("/delete/{clientHistoryId}")
    public ResponseEntity deleteClientHistory(@PathVariable("clientHistoryId") long clientHistoryId) {
        //Проверяем есть ли связанная с ClientHistory запись callRecord и если есть удаляем ее.
        Optional<CallRecord> callRecord = callRecordService.getByClientHistoryId(clientHistoryId);
        callRecord.ifPresent(callRecordService::delete);
        //Удаляем запись ClientHistory по Id
        clientHistoryService.deleteClientHistoryById(clientHistoryId);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    //Обновление записи по id клиента
    @PostMapping("/update/{clientId}")
    public ResponseEntity updateClientHistory(@PathVariable("clientId") long id, @RequestBody ClientHistory clientHistory) {
        clientHistory.setClient(clientRepository.getClientById(id));
        Optional<ClientHistory> result = clientHistoryService.addHistory(clientHistory);
        return ResponseEntity.ok(result);
    }
}
