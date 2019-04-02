package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ListMailing;
import com.ewp.crm.service.interfaces.ListMailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ListMailingRestController {

    private final ListMailingService listMailingService;

    @Autowired
    public ListMailingRestController(ListMailingService listMailingService) {
        this.listMailingService = listMailingService;
    }

    @PostMapping("/get/listMailing")
    public ResponseEntity<ListMailing> getListMailing(@RequestParam("listGroupId") Long id) {
        Optional<ListMailing> listMailing = listMailingService.getListMailingById(id);
        return listMailing.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/get/listMailing/{type}")
    public ResponseEntity<List<ListMailing>> getListMailingByType(@PathVariable String type) {
        return ResponseEntity.ok(listMailingService.getByType(type));
    }
}
