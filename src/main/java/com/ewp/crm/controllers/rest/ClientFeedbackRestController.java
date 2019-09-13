package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientFeedback;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.ClientFeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/client/feedback")
public class ClientFeedbackRestController {

    private static final Logger logger = LoggerFactory.getLogger(ClientFeedbackRestController.class);

    private final ClientRepository clientRepository;
    private final ClientFeedbackService clientFeedbackService;

    @Autowired
    public ClientFeedbackRestController(ClientRepository clientRepository, ClientFeedbackService clientFeedbackService) {
        this.clientRepository = clientRepository;
        this.clientFeedbackService = clientFeedbackService;
    }

    @GetMapping("/all")
    public ResponseEntity getAllFeedback() {
        List<ClientFeedback> allFeedback = clientFeedbackService.getAllFeedback();
        if(!allFeedback.isEmpty()) {
            return ResponseEntity.ok(allFeedback);
        } else {
            logger.info("feedbacks not found");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{clientId}")
    public ResponseEntity getFeedbackByClientId(@PathVariable("clientId") Long id) {
        List<ClientFeedback> allClientFeedback = clientFeedbackService.getAllByClientId(id);
        if(!allClientFeedback.isEmpty()) {
            return ResponseEntity.ok(allClientFeedback);
        } else {
            logger.info("client with id " + id + " has no feedback");
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity deleteFeedback(@PathVariable("feedbackId") Long id) {
        clientFeedbackService.deleteFeedback(id);
        logger.info("feedback id= " + id + "deleted");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/update",headers = "Content-type=application/json")
    public ResponseEntity updateFeedback(@RequestBody ClientFeedback feedback) {
        clientFeedbackService.updateFeedback(feedback);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity addFeedbackByClientId(@PathVariable("userId") Long id,@RequestBody ClientFeedback feedback) {
        Optional<ClientFeedback> newFeedback = clientFeedbackService.addFeedback(feedback);
        if(newFeedback.isPresent()) {
            ClientFeedback newClientFeedback = newFeedback.get();
            Client client = clientRepository.getClientById(id);
            client.addFeedback(newClientFeedback);
            clientRepository.saveAndFlush(client);
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            logger.warn("Error adding feedback");
            return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
        }
    }
}
