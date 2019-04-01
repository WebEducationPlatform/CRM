package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.EmailExtra;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.EmailExtraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    } // /rest/emailextra/getByClient/2

    @GetMapping(value = "/delete/{emailExtraId}")
    public ResponseEntity delEmailExtras(@PathVariable Long emailExtraId) {
        if (emailExtraService.get(emailExtraId) == null) {
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        } else {
            emailExtraService.delete(emailExtraId);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } // /rest/emailextra/delete/1

}
