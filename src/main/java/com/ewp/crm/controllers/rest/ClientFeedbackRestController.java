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

@RestController
@RequestMapping("/client/feedback/rest")
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
        if (allFeedback == null || allFeedback.isEmpty()) {
            logger.error("not found feedback");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allFeedback);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity getFeedbackByClientId(@PathVariable("clientId")Long id) {
        List<ClientFeedback> clientFeedback = clientFeedbackService.getAllByClientId(id);
        if (clientFeedback == null || clientFeedback.isEmpty()) {
            logger.error("no more feedback");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(clientFeedback);
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity deleteFeedback(@PathVariable("feedbackId")Long id) {
        clientFeedbackService.deleteFeedback(id);
        logger.info("feedback id="+id+" deleted");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/update",headers = "Content-type=application/json")
    public ResponseEntity updateFeedback(@RequestBody ClientFeedback feedback) {
        clientFeedbackService.updateFeedback(feedback);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity addFeedbackByClientId(@PathVariable("userId")Long id,@RequestBody ClientFeedback feedback) {
        ClientFeedback newFeedback = clientFeedbackService.addFeedback(feedback);
        Client client = clientRepository.getClientById(id);
        client.addFeedback(newFeedback);
        clientRepository.saveAndFlush(client);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
