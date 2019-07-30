package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ListMailingType;
import com.ewp.crm.service.interfaces.ListMailingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/listMailingType")
public class ListMailingTypeRestController {

    private final ListMailingTypeService listMailingTypeService;

    @Autowired
    public ListMailingTypeRestController(ListMailingTypeService listMailingTypeService) {
        this.listMailingTypeService = listMailingTypeService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<ListMailingType>> getListMailingTypes() {
        return ResponseEntity.ok(listMailingTypeService.getAll());
    }

    @PostMapping("/add")
    public ResponseEntity addListMailingType(@RequestParam String name) {
        listMailingTypeService.add(new ListMailingType(name));
        return new ResponseEntity(HttpStatus.OK);
    }
}
