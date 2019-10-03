package com.ewp.crm.controllers.rest.admin;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/rest/admin/client")
public class AdminRestClientController {

    private static Logger logger = LoggerFactory.getLogger(AdminRestClientController.class);

    private final ClientService clientService;
    private final ClientHistoryService clientHistoryService;
    private final StatusService statusService;
    private final StudentService studentService;

    @Autowired
    public AdminRestClientController(ClientService clientService,
                                     ClientHistoryService clientHistoryService,
                                     StatusService statusService, StudentService studentService) {
        this.clientService = clientService;
        this.clientHistoryService = clientHistoryService;
        this.statusService = statusService;
        this.studentService = studentService;
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    public ResponseEntity addClient(@RequestBody Client client,
                                    @AuthenticationPrincipal User userFromSession) {
        Optional<Status> status = statusService.get(client.getStatus().getName());
        status.ifPresent(client::setStatus);
        clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.ADD).ifPresent(client::addHistory);
        clientService.addClient(client, userFromSession);
        studentService.addStudentForClient(client);
        logger.info("{} has added client: id {}, email {}", userFromSession.getFullName(), client.getId(), client.getEmail().orElse("not found"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'MENTOR', 'HR')")
    public ResponseEntity updateClient(@RequestBody Client currentClient,
                                       @AuthenticationPrincipal User userFromSession) {
        Client clientFromDB = clientService.get(currentClient.getId());
        if (currentClient.equals(clientFromDB)) {
            logger.info("{} has no need to update client: id {}, email {}", currentClient.getId(), currentClient.getEmail().orElse("not found"));
            return ResponseEntity.noContent().build();
        } else {
            clientService.updateClient(currentClient);
            logger.info("{} has updated client: id {}, email {}", currentClient.getId(), currentClient.getEmail().orElse("not found"));
            clientHistoryService.createHistory(userFromSession, clientFromDB, currentClient, ClientHistory.Type.UPDATE).ifPresent(currentClient::addHistory);

            return ResponseEntity.ok(HttpStatus.OK);
        }
    }
    
    @GetMapping(value = "/remove")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    public ResponseEntity removeClient(@RequestParam(name = "clientId") Long clientId,
                                       @AuthenticationPrincipal User userFromSession) {
        Client clientFromDB = clientService.get(clientId);
        if (Objects.isNull(clientFromDB)) {
            return ResponseEntity.notFound().build();
        }
        clientService.delete(clientId);

        logger.info("{} has delete client: id {}, email {}", userFromSession.getFullName(), clientFromDB.getId(), clientFromDB.getEmail().orElse("not found"));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}