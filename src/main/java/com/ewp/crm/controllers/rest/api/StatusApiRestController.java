package com.ewp.crm.controllers.rest.api;


import com.ewp.crm.models.Status;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("rest/api/status")
public class StatusApiRestController {

    private static Logger logger = LoggerFactory.getLogger(StatusApiRestController.class);

    private final StatusService statusService;

    @Autowired
    public StatusApiRestController(StatusService statusService) {
        this.statusService = statusService;
    }

    @PostMapping(value = "/{statusName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addStatus(@PathVariable String statusName) {

        if (statusName == null || statusName.equals("")) {
            return (ResponseEntity) ResponseEntity.badRequest();
        }

        Status status = new Status(statusName);
        statusService.add(status);
        logger.info("Was added new status with name: " + statusName);
        return ResponseEntity.ok(statusService.getStatusByName(statusName));
    }

    @PutMapping
    public ResponseEntity updateStatus(@RequestBody Status status) {
        if (status.getId() != null) {
            statusService.update(status);
            logger.info("Status {} was updated...", status.getName());
        } else {
            return (ResponseEntity) ResponseEntity.notFound();
        }
        return ResponseEntity.ok(statusService.getStatusByName(status.getName()));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteStatus(@PathVariable Long id) {
        statusService.delete(id);
        return ResponseEntity.ok("Status was deleted successfully...");
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getStatusById(@PathVariable Long id) {
        if (statusService.get(id).get() == null) {
            return (ResponseEntity) ResponseEntity.notFound();
        }
        return ResponseEntity.ok(statusService.get(id).get());
    }

    @GetMapping(value = "/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getStatusByName(@PathVariable String name) {
        if (statusService.get(name).get() == null) {
            return (ResponseEntity) ResponseEntity.notFound();
        }
        return ResponseEntity.ok(statusService.getStatusByName(name).get());
    }
}
