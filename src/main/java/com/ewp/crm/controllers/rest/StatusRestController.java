package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.StatusDto;
import com.ewp.crm.models.dto.StatusDtoForBoard;
import com.ewp.crm.models.dto.StatusPositionIdNameDTO;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final UserStatusService userStatusService;
    private final SendMailsController sendMailsController;
    private final MessageTemplateService messageTemplateService;

    @Autowired
    public StatusRestController(StatusService statusService, ClientService clientService,
                                ClientHistoryService clientHistoryService, NotificationService notificationService,
                                StudentService studentService, StudentStatusService studentStatusService,
                                ClientStatusChangingHistoryService clientStatusChangingHistoryService,
                                RoleService roleService, UserStatusService userStatusService,
                                SendMailsController sendMailsController,
                                MessageTemplateService messageTemplateService) {
        this.statusService = statusService;
        this.clientService = clientService;
        this.clientHistoryService = clientHistoryService;
        this.notificationService = notificationService;
        this.studentService = studentService;
        this.studentStatusService = studentStatusService;
        this.clientStatusChangingHistoryService = clientStatusChangingHistoryService;
        this.roleService = roleService;
        this.userStatusService = userStatusService;
        this.sendMailsController = sendMailsController;
        this.messageTemplateService = messageTemplateService;
    }

    @GetMapping
    public ResponseEntity<List<Status>> getAllClientStatuses() {
        return ResponseEntity.ok(statusService.getAll());
    }

    @GetMapping(value = "/dto/for-mailing", produces = MediaType.APPLICATION_JSON_VALUE)
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
        Optional<Status> statusOptional = statusService.get(statusName);
        userStatusService.addStatusForAllUsers(statusOptional.get().getId());
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
                sendNotificationClientChangeStatus(currentClient, userFromSession);
                logger.info("{} has changed status of client with id: {} to status id: {}", userFromSession.getFullName(), clientId, statusId);
                return ResponseEntity.ok().build();
            }
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        clientService.updateClient(currentClient);
        notificationService.deleteNotificationsByClient(currentClient);
        sendNotificationClientChangeStatus(currentClient, userFromSession);
        logger.info("{} has changed status of client with id: {} to status id: {}", userFromSession.getFullName(), clientId, statusId);
        return ResponseEntity.ok().build();
    }

    private void sendNotificationClientChangeStatus(Client currentClient,
                                                    User userFromSession) {
        List<String> emailList = currentClient.getClientEmails();
        if (!emailList.isEmpty()) {
            Optional<MessageTemplate> messageTemplateOpt = messageTemplateService.getByName("Изменение статуса");
            for (String email : emailList) {
                sendMailsController.sendMails(
                        "email",
                        email,
                        messageTemplateOpt.get().getOtherText(),
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm МСК").format(LocalDateTime.now()),
                        "managerPage",
                        "1",
                        userFromSession);
            }
        }
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
    public ResponseEntity<List<StatusPositionIdNameDTO>> getAllStatusPositions(@AuthenticationPrincipal User currentUser) {
        List<StatusPositionIdNameDTO> statusPositionIdNameDTOList = statusService.getAllStatusesMinDTOWhichAreNotInvisible(currentUser);
        statusPositionIdNameDTOList.sort(Comparator.comparingLong(StatusPositionIdNameDTO::getPosition));
        return ResponseEntity.ok(statusPositionIdNameDTOList);
    }

    @RequestMapping(value = "/position/change", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity updateStatusesPositions(@RequestBody List<StatusPositionIdNameDTO> statusesPositionIdsNameDTO,
                                                  @AuthenticationPrincipal User currentUser) {
        Long in = Long.valueOf(1);
        for(StatusPositionIdNameDTO statusPositionIdNameDTO : statusesPositionIdsNameDTO) {
            userStatusService.updateUserStatus(currentUser.getId(), statusPositionIdNameDTO.getId(), false, in);
            in++;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}