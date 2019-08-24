package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Mentor;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.MentorService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class MentorRestController {
    private final MentorService mentorService;
    private final UserService userService;
    private static Logger logger = LoggerFactory.getLogger(MentorRestController.class);

    @Autowired
    public MentorRestController(MentorService mentorService, UserService userService){
        this.mentorService = mentorService;
        this.userService = userService;
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

    @GetMapping(value = "/admin/rest/mentor/student/quantity/{id}")
    public int getQuantityStudentsForMentor(@PathVariable long id){
        return mentorService.getQuantityStudentsByMentorId(id);
    }

//    @PreAuthorize("hasAnyAuthority('OWNER')")
    @PostMapping(value = "/mentor/rest/user/update/{id}")
    public ResponseEntity updateUser(@PathVariable long id) {
//        mentorService.updateQuantityStudentsByMentorId(id,20);
        mentorService.updateUserAsMentorWithQuantityStudents(id,20);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}