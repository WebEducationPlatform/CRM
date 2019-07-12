package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Mentor;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class MentorRestController {
    private MentorService mentorService;

    @Autowired
    public MentorRestController(MentorService mentorService){
        this.mentorService = mentorService;
    }

    @PostMapping(value = "/mentor/showOnlyMyClients")
    public ResponseEntity showOnlyMyClients(@RequestParam(name = "showOnlyMyClients") Boolean showOnlyMyClients, @AuthenticationPrincipal User userFromSession){
        Mentor mentor = mentorService.getMentorById(userFromSession.getId());
        if (mentor == null){
            mentorService.saveMentorShowAllFieldAndUserIdField(showOnlyMyClients, userFromSession.getId());
        } else {
            mentorService.updateMentorShowAllFieldAndUserIdField(showOnlyMyClients, userFromSession.getId());
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(value = "/mentor/showOnlyMyClient/{id}")
    public ResponseEntity getShowAllMyClientsByIdUser(@PathVariable("id") Long id){
        Boolean mentorShowAllClients = mentorService.getMentorShowAllClientsById(id);
        return  ResponseEntity.ok(mentorShowAllClients);
    }
}