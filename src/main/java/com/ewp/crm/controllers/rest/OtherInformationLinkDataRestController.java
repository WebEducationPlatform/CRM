package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.IdAndHash;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.OtherInformationLinkDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otherInformation/link")
public class OtherInformationLinkDataRestController {
    private static final Logger logger = LoggerFactory.getLogger(OtherInformationLinkDataRestController.class);
    private final OtherInformationLinkDataService otherInformationLinkDataService;
    private final ClientService clientService;

    @Autowired
    public OtherInformationLinkDataRestController(OtherInformationLinkDataService otherInformationLinkDataService, ClientService clientService) {
        this.otherInformationLinkDataService = otherInformationLinkDataService;
        this.clientService = clientService;
    }


    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity createOtherInformationLinkData(@RequestBody IdAndHash idAndHash, @AuthenticationPrincipal User userFromSession) {
        Long clientId = idAndHash.getClientId();
        Client client = clientService.get(clientId);
        if (client.getOtherInformationLinkData() == null) {
            clientService.setOtherInformationLink(clientId, idAndHash.getHash());
            logger.info("{} create unique other information link for client id = {}", userFromSession.getFullName(), clientId);
            return new ResponseEntity(HttpStatus.OK);
        } else {
            logger.error("Client with id {} already have other information", clientId);
            return new ResponseEntity(HttpStatus.ALREADY_REPORTED);
        }
    }
}