package com.ewp.crm.controllers.rest;


import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.VKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/init")
public class InitRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VKService vkService;

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

    @GetMapping(value = "/getvkinfo")
    public ResponseEntity getVkinfoById() {
        return ResponseEntity.ok(vkService.getProfileInfoById("231037150").orElseGet(HashMap::new));
    }

}
