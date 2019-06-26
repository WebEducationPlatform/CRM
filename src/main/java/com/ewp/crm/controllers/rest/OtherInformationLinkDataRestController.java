package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.repository.interfaces.OtherInformationLinkDAO;
import com.ewp.crm.service.interfaces.ClientOtherInformationService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.OtherInformationLinkDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/otherInformation/link")
public class OtherInformationLinkDataRestController {
    private static final Logger logger = LoggerFactory.getLogger(OtherInformationLinkDataRestController.class);
    private final OtherInformationLinkDataService otherInformationLinkDataService;
    private final ClientService clientService;
    private final ClientOtherInformationService clientOtherInformationService;
    private final OtherInformationLinkDAO otherInformationLinkDAO;

    @Autowired
    public OtherInformationLinkDataRestController(OtherInformationLinkDataService otherInformationLinkDataService,
                                                  ClientService clientService,
                                                  ClientOtherInformationService clientOtherInformationService,
                                                  OtherInformationLinkDAO otherInformationLinkDAO) {
        this.otherInformationLinkDataService = otherInformationLinkDataService;
        this.clientService = clientService;
        this.clientOtherInformationService = clientOtherInformationService;
        this.otherInformationLinkDAO = otherInformationLinkDAO;
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

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteVkRequestFormById(@PathVariable("id") Long clientId) {
        clientOtherInformationService.deleteAllByClientId(clientId);
        otherInformationLinkDAO.deleteOtherInformationLinkByClientId(clientId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/link")
    public ResponseEntity createLinkToClient(@RequestBody OtherInformationInputValues data) {
        if (otherInformationLinkDataService.existsByHash(data.getHash())) {
            Long clientId = otherInformationLinkDataService.getByHash(data.getHash()).get().getClient().getId();
            for (Map<String, String> value : data.getOtherInformationInputValues()) {
                String name = value.get("name");
                ClientOtherInformation clientOtherInformation = clientOtherInformationService.getClientOtherInformationByNameAndClientId(name, null);
                ClientOtherInformation newClientOtherInformationToOurClient = new ClientOtherInformation(name, clientOtherInformation.getTypeField());
                if (clientOtherInformation.getTypeField().equals("CHECKBOX")) {
                    String booleanValue = value.get("value");
                    if (booleanValue.equals("true")) {
                        newClientOtherInformationToOurClient.setCheckboxValue(true);
                    } else {
                        newClientOtherInformationToOurClient.setCheckboxValue(false);
                    }
                } else {
                    String textValue = value.get("value");
                    newClientOtherInformationToOurClient.setTextValue(textValue);
                }
                newClientOtherInformationToOurClient.setClientId(clientId);
                clientOtherInformationService.save(newClientOtherInformationToOurClient);
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}