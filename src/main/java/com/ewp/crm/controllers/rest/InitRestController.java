package com.ewp.crm.controllers.rest;


import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/init")
public class InitRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = "/updatepassword")
    public ResponseEntity updateVkTrackedClub() {
        List<User> listUsers = userService.getAll();
        for (User user : listUsers) {
            String curPass = user.getPassword();
            if (!curPass.startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(curPass));
                userService.update(user);
            }
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
