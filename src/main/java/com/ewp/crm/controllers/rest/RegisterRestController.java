package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
public class RegisterRestController {

    private static Logger logger = LoggerFactory.getLogger(RegisterRestController.class);

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public ResponseEntity addUser(@Valid @RequestBody User user) {
        if ( user.isVerified() || user.isEnabled()) {
            logger.warn("CRM been attempt of hacking");
            return ResponseEntity.status(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS).build();
        }
        userService.add(user);
        logger.info("{} has register user: email {}", user.getFullName(), user.getEmail());
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
