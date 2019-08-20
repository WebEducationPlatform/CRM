package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/public/info")
public class PublicStudentInfoRestController {

    private ClientService clientService;

    @Autowired
    public PublicStudentInfoRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/{email}")
    public ResponseEntity<Client> getClient(@PathVariable("email") String email) {
        Optional<Client> optionalClient = clientService.getClientByEmail(email);
        return optionalClient.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
