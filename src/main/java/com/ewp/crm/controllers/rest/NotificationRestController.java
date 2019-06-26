package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.NotificationService;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
@RequestMapping("/user/notification")
public class NotificationRestController {

    private final ClientService clientService;
    private final NotificationService notificationService;
    private final ClientHistoryService clientHistoryService;
    @Autowired
    private UserService userService;

    @Autowired
    public NotificationRestController(ClientService clientService,
                                      NotificationService notificationService,
                                      ClientHistoryService clientHistoryService) {
        this.clientService = clientService;
        this.notificationService = notificationService;
        this.clientHistoryService = clientHistoryService;
    }

    @GetMapping("/sms/error/{clientId}")
    public ResponseEntity getSMSErrorsByClient(@PathVariable("clientId") Long id,
                                               @AuthenticationPrincipal User userFromSession) {
        List<Notification> list = notificationService.getByUserToNotifyAndTypeAndClient(userFromSession, Notification.Type.SMS, clientService.get(id));
        if (list == null || list.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping("/sms/clear/{clientId}")
    public ResponseEntity clearClientSmsNotifications(@PathVariable("clientId") long id,
                                                      @AuthenticationPrincipal User userFromSession) {
        Client client = clientService.get(id);
        notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.SMS, client, userFromSession);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/comment/clear/{clientId}")
    public ResponseEntity markAsRead(@PathVariable("clientId") long id,
                                     @AuthenticationPrincipal User userFromSession) {
        Client client = clientService.get(id);
        //client.setPostponeComment("");
        List<Notification> notifications = notificationService.getByUserToNotifyAndTypeAndClient(userFromSession, Notification.Type.POSTPONE, client);
        notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.COMMENT, client, userFromSession);
        notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.POSTPONE, client, userFromSession);
        notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.NEW_USER, client, userFromSession);
        for (Notification notification : notifications) {
            if (notification.getType() == Notification.Type.POSTPONE) {
                Optional<ClientHistory> clientHistory = clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.NOTIFICATION);
                if (clientHistory.isPresent()) {
                    clientHistory.get().setClient(client);
                    clientHistoryService.addHistory(clientHistory.get());
                }
            }
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/comment/cleanAll")
    public List<Client> markAsReadAll(@AuthenticationPrincipal User userFromSession) {
        List<Client> clients = notificationService.getClientWithNotification();
        List<Notification> notifications;
        for (Client client : clients) {
            notifications = notificationService.getByUserToNotifyAndTypeAndClient(userFromSession, Notification.Type.POSTPONE, client);
            notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.COMMENT, client, userFromSession);
            notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.POSTPONE, client, userFromSession);
            notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.NEW_USER, client, userFromSession);
            for (Notification notification : notifications) {
                if (notification.getType() == Notification.Type.POSTPONE) {
                    Optional<ClientHistory> clientHistory = clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.NOTIFICATION);
                    if (clientHistory.isPresent()) {
                        clientHistory.get().setClient(client);
                        clientHistoryService.addHistory(clientHistory.get());
                    }
                }
            }
        }
        return clients;
    }

    @PostMapping(value = "/comment/cleanAllNewUserNotify")
    public ResponseEntity markAsReadAllNewUserNotify(@AuthenticationPrincipal User userFromSession) {
        if (userFromSession.isNewClientNotifyIsEnabled()) {
            userFromSession.setNewClientNotifyIsEnabled(false);
            List<Client> clients = clientService.getAllClients();
            for (Client client : clients) {
                notificationService.deleteByTypeAndClientAndUserToNotify(Notification.Type.NEW_USER, client, userFromSession);
            }
        } else {
            userFromSession.setNewClientNotifyIsEnabled(true);
        }
        userService.update(userFromSession);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/postpone/getAll")
    public ResponseEntity getPostnopeNotify(@RequestParam(name = "clientId") long id,
                                            @AuthenticationPrincipal User userFromSession) {
        Optional<Client> client = clientService.getClientByID(id);
        if (client.isPresent()) {
            List<Notification> notifications = notificationService.getByUserToNotifyAndTypeAndClient(userFromSession, Notification.Type.POSTPONE, client.get());
            return ResponseEntity.ok(notifications);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
