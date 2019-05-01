package com.ewp.crm.controllers.rest.admin;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/admin/rest/client")
public class AdminRestClientController {

    private static Logger logger = LoggerFactory.getLogger(AdminRestClientController.class);

    private final ClientService clientService;
    private final ClientHistoryService clientHistoryService;
    private final StatusService statusService;
    private final StudentService studentService;
    private final AssignSkypeCallService assignSkypeCallService;

    @Autowired
    public AdminRestClientController(AssignSkypeCallService assignSkypeCallService,
                                     ClientService clientService,
                                     ClientHistoryService clientHistoryService,
                                     StatusService statusService, StudentService studentService) {
        this.assignSkypeCallService = assignSkypeCallService;
        this.clientService = clientService;
        this.clientHistoryService = clientHistoryService;
        this.statusService = statusService;
        this.studentService = studentService;
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity addClient(@RequestBody Client client,
                                    @AuthenticationPrincipal User userFromSession) {
            Optional<Status> status = statusService.get(client.getStatus().getName());
            status.ifPresent(client::setStatus);
            clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.ADD).ifPresent(client::addHistory);
            clientService.addClient(client);
            studentService.addStudentForClient(client);
            logger.info("{} has added client: id {}, email {}", userFromSession.getFullName(), client.getId(), client.getEmail());

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity updateClient(@RequestBody Client currentClient,
                                       @AuthenticationPrincipal User userFromSession) {
        Client clientFromDB = clientService.get(currentClient.getId());
        currentClient.setWhatsappMessages(clientFromDB.getWhatsappMessages());
        currentClient.setHistory(clientFromDB.getHistory());
        currentClient.setComments(clientFromDB.getComments());
        currentClient.setOwnerUser(clientFromDB.getOwnerUser());
        currentClient.setStatus(clientFromDB.getStatus());
        currentClient.setDateOfRegistration(ZonedDateTime.parse(clientFromDB.getDateOfRegistration().toString()));
        currentClient.setSmsInfo(clientFromDB.getSmsInfo());
        currentClient.setNotifications(clientFromDB.getNotifications());
        currentClient.setCanCall(clientFromDB.isCanCall());
        currentClient.setCallRecords(clientFromDB.getCallRecords());
        currentClient.setClientDescriptionComment(clientFromDB.getClientDescriptionComment());
        currentClient.setLiveSkypeCall(clientFromDB.isLiveSkypeCall());
        currentClient.setState(clientFromDB.getState());
        if (currentClient.equals(clientFromDB)) {
            return ResponseEntity.noContent().build();
        }
        clientHistoryService.createHistory(userFromSession, clientFromDB, currentClient, ClientHistory.Type.UPDATE).ifPresent(currentClient::addHistory);
        clientService.updateClient(currentClient);
        logger.info("{} has updated client: id {}, email {}", userFromSession.getFullName(), currentClient.getId(), currentClient.getEmail());
        return ResponseEntity.ok(HttpStatus.OK);
    }

}