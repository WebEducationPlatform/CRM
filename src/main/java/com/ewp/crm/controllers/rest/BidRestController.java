package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.VkBid;
import com.ewp.crm.service.interfaces.VkBidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class BidRestController {
    private final VkBidService vkBidService;

    @Autowired
    public BidRestController(VkBidService vkBidService) {
        this.vkBidService = vkBidService;
    }

    @RequestMapping(value = "/bid/create", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody VkBid vkBid) {
        vkBidService.add(vkBid);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/bid/getBid/{id}", method = RequestMethod.GET)
    public ResponseEntity<VkBid> getUserById(@PathVariable("id") long id) {
        return ResponseEntity.ok(vkBidService.get(id));
    }


    @RequestMapping(value = "/bid/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<VkBid> updateUser(@PathVariable("id") int id, @RequestBody VkBid vkBid) {
        vkBid.setId(id);
        vkBidService.update(vkBid);
        return new ResponseEntity<>(vkBid, HttpStatus.OK);
    }

    @RequestMapping(value = "/bid/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<Long> deleteUser(@PathVariable("id") long id) {
        vkBidService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}