package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.EmailExtra;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.EmailExtraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/emailextra")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class EmailExtraRestController {

    private static Logger logger = LoggerFactory.getLogger(EmailExtraRestController.class);

    private final ClientService clientService;
    private final EmailExtraService emailExtraService;

    @Autowired
    public EmailExtraRestController(ClientService clientService,
                                 EmailExtraService emailExtraService) {
        this.clientService = clientService;
        this.emailExtraService = emailExtraService;
    }

    @GetMapping(value = "/getByClient/{clientId}")
    public ResponseEntity<List<EmailExtra>> getEmailExtrasByClient(@PathVariable Long clientId) {
        List<EmailExtra> emailExtras = emailExtraService.getAllEmailsExtraByClient(clientService.get(clientId));
        return ResponseEntity.ok(emailExtras);
    }

    @GetMapping(value = "/delete/{emailExtraId}")
    public ResponseEntity delEmailExtras(@PathVariable Long emailExtraId) {
        if (emailExtraService.get(emailExtraId) == null) {
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        } else {
            emailExtraService.delete(emailExtraId);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = "/add")
    public ResponseEntity<EmailExtra> addEmailExtra(@RequestParam(name = "clientId") Long clientId,
                                              @RequestParam(name = "emailExtra") String mail) {
        Client client = clientService.get(clientId);
        if (client == null) {
            logger.error("Can`t add additional email, client with id {} not found", clientId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        EmailExtra emailExtra = new EmailExtra(mail, client);
        emailExtraService.add(emailExtra);
        return ResponseEntity.status(HttpStatus.CREATED).body(emailExtra);
    }

    @PostMapping(value = "/edit")
    public ResponseEntity<EmailExtra> editEmailExtra(@RequestParam(name = "id") Long emailExtraId,
                                                    @RequestParam(name = "emailExtra") String mail) {
        EmailExtra emailExtra = emailExtraService.get(emailExtraId);
        if (emailExtra == null) {
            logger.error("Can`t edit additional email, emailExtra with id {} not found", mail);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            emailExtra.setEmailExtra(mail);
            emailExtraService.update(emailExtra);
            return ResponseEntity.status(HttpStatus.OK).body(emailExtra);
        }
    }

}
