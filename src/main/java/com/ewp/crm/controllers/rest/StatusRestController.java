package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.StatusDto;
import com.ewp.crm.models.dto.StatusPositionIdNameDTO;
import com.ewp.crm.models.dto.StatusDtoForBoard;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ewp.crm.util.Constants.*;

@RestController
@RequestMapping("/rest/status")
public class StatusRestController {

    private static Logger logger = LoggerFactory.getLogger(StatusRestController.class);

    private final StatusService statusService;
    private final ClientService clientService;
    private final ClientHistoryService clientHistoryService;
    private final NotificationService notificationService;
    private final StudentService studentService;
    private final StudentStatusService studentStatusService;
    private final ClientStatusChangingHistoryService clientStatusChangingHistoryService;
    private final RoleService roleService;

    @Autowired
    public StatusRestController(StatusService statusService, ClientService clientService,
                                ClientHistoryService clientHistoryService, NotificationService notificationService,
                                StudentService studentService, StudentStatusService studentStatusService,
                                ClientStatusChangingHistoryService clientStatusChangingHistoryService,
                                RoleService roleService) {
        this.statusService = statusService;
        this.clientService = clientService;
        this.clientHistoryService = clientHistoryService;
        this.notificationService = notificationService;
        this.studentService = studentService;
        this.studentStatusService = studentStatusService;
        this.clientStatusChangingHistoryService = clientStatusChangingHistoryService;
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<Status>> getAllClientStatuses() {
        return ResponseEntity.ok(statusService.getAll());
    }

    @GetMapping(value="/dto/for-mailing", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StatusDto>> getAllStatusDtoForMailing() {
        return ResponseEntity.ok(statusService.getStatusesForMailing());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'MENTOR')")
    public ResponseEntity<List<Client>> getStatusByID(@PathVariable Long id) {
        return statusService.get(id).map(s -> ResponseEntity.ok(clientService.getAllClientsByStatus(s))).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/all/invisible")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
    public ResponseEntity<List<StatusDtoForBoard>> getAllInvisibleStatuses(@AuthenticationPrincipal User userFromSession) {
        List<Role> sessionRoles = userFromSession.getRole();
        Role role = roleService.getRoleByName(ROLE_NAME_USER);
        if (sessionRoles.contains(roleService.getRoleByName(ROLE_NAME_MENTOR))) {
            role = roleService.getRoleByName(ROLE_NAME_MENTOR);
        }
        if (sessionRoles.contains(roleService.getRoleByName(ROLE_NAME_HR))) {
            role = roleService.getRoleByName(ROLE_NAME_HR);
        }
        if (sessionRoles.contains(roleService.getRoleByName(ROLE_NAME_ADMIN))) {
            role = roleService.getRoleByName(ROLE_NAME_ADMIN);
        }
        if (sessionRoles.contains(roleService.getRoleByName(ROLE_NAME_OWNER))) {
            role = roleService.getRoleByName(ROLE_NAME_OWNER);
        }
        List<StatusDtoForBoard> statuses = statusService.getStatusesForBoardByUserAndRole(userFromSession, role);
        return ResponseEntity.ok(statuses.stream().filter(StatusDtoForBoard::getInvisible).collect(Collectors.toList()));
    }

    @RequestMapping(value = "lost", method = {RequestMethod.POST}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Client>> getLostStudentByStatus(@RequestBody List<String> emails) {
        List<Client> clientsToEmails = clientService.getClientsByEmails(emails).orElse(Collections.emptyList());
        return ResponseEntity.ok(clientsToEmails);
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'MENTOR', 'HR')")
    public ResponseEntity addNewStatus(@RequestParam(name = "statusName") String statusName,
                                       @AuthenticationPrincipal User currentAdmin) {

        final Status status = new Status(statusName);
        statusService.add(status, currentAdmin.getRole());
        logger.info("{} has added status with name: {}", currentAdmin.getFullName(), statusName);
        return ResponseEntity.ok("Успешно добавлено");
    }

    @PostMapping(value = "/client/change")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
    public ResponseEntity changeClientStatus(@RequestParam(name = "statusId") Long statusId,
                                             @RequestParam(name = "clientId") Long clientId,
                                             @AuthenticationPrincipal User userFromSession) {
        Client currentClient = clientService.get(clientId);
        if (currentClient.getStatus().getId().equals(statusId)) {
            return ResponseEntity.ok().build();
        }
        Status lastStatus = currentClient.getStatus();
        statusService.get(statusId).ifPresent(currentClient::setStatus);
        clientHistoryService.createHistoryOfChangingStatus(userFromSession, currentClient, lastStatus).ifPresent(currentClient::addHistory);
        ClientStatusChangingHistory clientStatusChangingHistory = new ClientStatusChangingHistory(
                ZonedDateTime.now(),
                lastStatus,
                currentClient.getStatus(),
                currentClient,
                userFromSession);
        clientStatusChangingHistoryService.add(clientStatusChangingHistory);
        if (!lastStatus.isCreateStudent() && currentClient.getStatus().isCreateStudent()) {
            Optional<Student> newStudent = studentService.addStudentForClient(currentClient);
            if (newStudent.isPresent()) {
                clientHistoryService.creteStudentHistory(userFromSession, ClientHistory.Type.ADD_STUDENT).ifPresent(currentClient::addHistory);
                clientService.updateClient(currentClient);
                notificationService.deleteNotificationsByClient(currentClient);
                logger.info("{} has changed status of client with id: {} to status id: {}", userFromSession.getFullName(), clientId, statusId);
                return ResponseEntity.ok().build();
            }
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        clientService.updateClient(currentClient);
        notificationService.deleteNotificationsByClient(currentClient);
        logger.info("{} has changed status of client with id: {} to status id: {}", userFromSession.getFullName(), clientId, statusId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/client/delete")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
    public ResponseEntity deleteClientStatus(@RequestParam("clientId") long clientId,
                                             @AuthenticationPrincipal User userFromSession) {
        Client client = clientService.get(clientId);
        if (client == null) {
            logger.error("Can`t delete client status, client with id = {} not found", clientId);
            return ResponseEntity.notFound().build();
        }
        Status lastStatus = client.getStatus();
        statusService.get("deleted").ifPresent(client::setStatus);
        clientHistoryService.createHistoryOfChangingStatus(userFromSession, client, lastStatus).ifPresent(client::addHistory);
        ClientStatusChangingHistory clientStatusChangingHistory = new ClientStatusChangingHistory(
                ZonedDateTime.now(),
                lastStatus,
                client.getStatus(),
                client,
                userFromSession);
        clientStatusChangingHistoryService.add(clientStatusChangingHistory);
        clientService.updateClient(client);
        notificationService.deleteNotificationsByClient(client);
        logger.info("{} delete client with id = {} in status {}", userFromSession.getFullName(), client.getId(), lastStatus.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/position/change")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity changePositionOfTwoStatuses(@RequestParam("sourceId") long sourceId,
                                                      @RequestParam("destinationId") long destinationId) {
        Optional<Status> sourceStatus = statusService.get(sourceId);
        Optional<Status> destinationStatus = statusService.get(destinationId);
        if (!sourceStatus.isPresent() || !destinationStatus.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Long tempPosition = sourceStatus.get().getPosition();
        sourceStatus.get().setPosition(destinationStatus.get().getPosition());
        destinationStatus.get().setPosition(tempPosition);
        statusService.update(sourceStatus.get());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/create-student")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
    public HttpStatus setCreateStudent(@RequestParam("id") Long id, @RequestParam("create") Boolean create) {
        Optional<Status> status = statusService.get(id);
        if (status.isPresent()) {
            status.get().setCreateStudent(create);
            statusService.update(status.get());
            return HttpStatus.OK;
        }
        return HttpStatus.NOT_FOUND;
    }

    @PostMapping("/client/changeByName")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
    public ResponseEntity changeStatusByName(@RequestParam("newStatus") String newStatus,
                                             @RequestParam("clientId") Long clientId,
                                             @AuthenticationPrincipal User currentUser) {

        Optional<Status> st = statusService.getStatusByName(newStatus);
        if (st.isPresent()) {
            Long statusId = st.get().getId();
            return this.changeClientStatus(statusId, clientId, currentUser);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all/dto-position-id")
    public ResponseEntity<List<StatusPositionIdNameDTO>> getAllStatusPositions() {
        List<StatusPositionIdNameDTO> statusPositionIdNameDTOList = statusService.getAllStatusesMinDTOWhichAreNotInvisible();
        statusPositionIdNameDTOList.sort(Comparator.comparingLong(StatusPositionIdNameDTO::getPosition));
        return ResponseEntity.ok(statusPositionIdNameDTOList);
    }

    @RequestMapping(value = "/position/change", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity updateStatusesPositions(@RequestBody List<StatusPositionIdNameDTO> statusesPositionIdsNameDTO) {
        for (int out = statusesPositionIdsNameDTO.size() - 1; out >= 1; out--){
            for (int in = 0; in < out; in++){
                if(statusesPositionIdsNameDTO.get(in).getPosition() > statusesPositionIdsNameDTO.get(in + 1).getPosition())    {
                    Long postitonFirst =   statusesPositionIdsNameDTO.get(in).getPosition();
                    Long postitonSecond =   statusesPositionIdsNameDTO.get(in + 1).getPosition();
                    Long idFirst = statusesPositionIdsNameDTO.get(in).getId();
                    Long idSecond = statusesPositionIdsNameDTO.get(in + 1).getId();
                    statusesPositionIdsNameDTO.get(in).setPosition(postitonSecond);
                    statusesPositionIdsNameDTO.get(in + 1).setPosition(postitonFirst);
                    Optional<Status> first = statusService.get(idFirst);
                    Optional<Status> second = statusService.get(idSecond);
                    first.get().setPosition(postitonSecond);
                    second.get().setPosition(postitonFirst);
                    statusService.update(first.get());
                    statusService.update(second.get());
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}