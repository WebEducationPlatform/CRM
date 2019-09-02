package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ClientOtherInformation;
import com.ewp.crm.models.OtherInformationMultipleCheckboxes;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientOtherInformationService;
import com.ewp.crm.service.interfaces.OtherInformationMultipleCheckboxesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@RestController
public class OtherInformationToClientRestController {
    private final ClientOtherInformationService clientOtherInformationService;
    private final OtherInformationMultipleCheckboxesService otherInformationMultipleCheckboxesService;

    @Autowired
    public OtherInformationToClientRestController(ClientOtherInformationService clientOtherInformationService, OtherInformationMultipleCheckboxesService otherInformationMultipleCheckboxesService) {
        this.clientOtherInformationService = clientOtherInformationService;
        this.otherInformationMultipleCheckboxesService = otherInformationMultipleCheckboxesService;
    }

    @GetMapping(value = "/otherInformation")
    @PreAuthorize("hasAnyAuthority('OWNER')")
    public ModelAndView clientOtherInformationAll(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("other-information");
        modelAndView.addObject("userCustomize", userFromSession);
        List<ClientOtherInformation> listClientOtherInformation = clientOtherInformationService.getAllClientOtherInformaionById(null);
        modelAndView.addObject("otherInformation", listClientOtherInformation);
        return modelAndView;
    }

    @RequestMapping(value = "/otherInformation/create", method = RequestMethod.POST)
    public ResponseEntity createClientOtherInformation(@RequestBody ClientOtherInformation clientOtherInformation) {
        for (OtherInformationMultipleCheckboxes oimc: clientOtherInformation.getOimc()) {
            otherInformationMultipleCheckboxesService.save(oimc);
        }
        clientOtherInformationService.addClientOtherInformation(clientOtherInformation);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/otherInformation/{id}", method = RequestMethod.GET)
    public ResponseEntity<ClientOtherInformation> getClientOtherInformationById(@PathVariable("id") Long id) {
        Optional<ClientOtherInformation> clientOtherInformation = clientOtherInformationService.getClientOtherInformationById(id);
        return clientOtherInformation.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @RequestMapping(value = "/otherInformation/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ClientOtherInformation> updateClientOtherInformation(@PathVariable("id") Long id, @RequestBody ClientOtherInformation clientOtherInformation) {
        ClientOtherInformation coi = clientOtherInformationService.getClientOtherInformationById(id).get();
        List<OtherInformationMultipleCheckboxes> oimcOriginal = coi.getOimc();
        for (OtherInformationMultipleCheckboxes oimc : oimcOriginal) {
            otherInformationMultipleCheckboxesService
                    .deleteOtherInformationMultipleCheckboxesByNameField(oimc.getNameField());
        }
        List<OtherInformationMultipleCheckboxes> oimcForUpdate = clientOtherInformation.getOimc();
        for (OtherInformationMultipleCheckboxes oimc : oimcForUpdate) {
            otherInformationMultipleCheckboxesService.save(oimc);
        }
        clientOtherInformation.setId(id);
        clientOtherInformation.setOimc(oimcForUpdate);
        clientOtherInformationService.updateClientOtherInformation(clientOtherInformation);
        return new ResponseEntity<>(clientOtherInformation, HttpStatus.OK);
    }

    @RequestMapping(value = "/otherInformation/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteClientOtherInformation(@PathVariable("id") Long id) {
        clientOtherInformationService.deleteClientOtherInformationById(id);
        return new ResponseEntity(HttpStatus.OK);
    }


    @RequestMapping(value = "/otherInformation/client/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ClientOtherInformation>> getListClientOtherInformationByClientId(@PathVariable("id") Long clientId) {
        List<ClientOtherInformation> otherInformationList;
        if (clientId.equals(0L)) {
            otherInformationList = clientOtherInformationService.getAllClientOtherInformaionById(null);
        } else {
            otherInformationList = clientOtherInformationService.getAllClientOtherInformaionById(clientId);
        }
        return !otherInformationList.isEmpty() ? ResponseEntity.ok(otherInformationList) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}