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

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addStatus(@RequestParam("statusName") String statusName) {

        Status status = new Status(statusName);
        statusService.add(status);
        logger.info("Was added new status with name: " + statusName);

        return ResponseEntity.ok(statusService.getStatusByName(statusName));
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateStatus(@RequestBody Status status) {

        if (status.getId() != null) {
            statusService.update(status);
            logger.info("Status {} was updated...", status.getName());
        } else {
            return (ResponseEntity) ResponseEntity.notFound();
        }

        return ResponseEntity.ok(statusService.getStatusByName(status.getName()));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteStatus(@PathVariable Long id) {

        statusService.delete(id);

        return ResponseEntity.ok("Status was deleted successfully...");
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getStatusById(@PathVariable Long id) {
        statusService.get(id);
        return ResponseEntity.ok(statusService.get(id));
    }

}
