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

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    CallRecordService callRecordService;

    @Autowired
    public ClientHistoryApiRestController(ClientHistoryService clientHistoryService) {
        this.clientHistoryService = clientHistoryService;
    }

    //Добавление записи по id клиента
    @PostMapping("/{clientId}")
    public ResponseEntity addClientHistory(@PathVariable("clientId") long id, @RequestBody ClientHistory clientHistory) {
        clientHistory.setClient(clientRepository.getClientById(id));
        Optional<ClientHistory> result = clientHistoryService.addHistory(clientHistory);
        return ResponseEntity.ok(result);
    }

    //Удаление записи по id истории
    @DeleteMapping("/{clientHistoryId}")
    public ResponseEntity deleteClientHistory(@PathVariable("clientHistoryId") long clientHistoryId) {
        //Проверяем есть ли связанная с ClientHistory запись callRecord и если есть удаляем ее.
        Optional<CallRecord> callRecord = callRecordService.getByClientHistory_Id(clientHistoryId);
        if (callRecord.isPresent()) {
            callRecordService.delete(callRecord.get());
        }
        //Удаляем запись ClientHistory по Id
        clientHistoryService.deleteClientHistoryById(clientHistoryId);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    //Обновление записи по id клиента
    @PutMapping("/{clientId}")
    public ResponseEntity updateClientHistory(@PathVariable("clientId") long id, @RequestBody ClientHistory clientHistory) {
        clientHistory.setClient(clientRepository.getClientById(id));
        Optional<ClientHistory> result = clientHistoryService.addHistory(clientHistory);
        return ResponseEntity.ok(result);
    }
}
