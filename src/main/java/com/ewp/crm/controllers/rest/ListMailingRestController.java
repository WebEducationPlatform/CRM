package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ListMailing;
import com.ewp.crm.service.interfaces.ListMailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ListMailingRestController {

    private final ListMailingService listMailingService;

    @Autowired
    public ListMailingRestController(ListMailingService listMailingService) {
        this.listMailingService = listMailingService;
    }

    @PostMapping("/get/listMailing")
    public ResponseEntity<ListMailing> getListMailing(@RequestParam("listGroupName") String listName) {
        return ResponseEntity.ok(listMailingService.getByListName(listName));

    }
}
