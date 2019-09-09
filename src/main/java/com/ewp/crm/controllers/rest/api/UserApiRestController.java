package com.ewp.crm.controllers.rest.api;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/api/user/")
@Transactional
public class UserApiRestController {

    @Autowired
    UserService userService;

    @Autowired
    UserStatusService userService;

    @Autowired
    private ObjectMapper mapper;

    @GetMapping("/")
    public List<User> getAllUsers() {
        List<User> userList = userService.getAll();
        return userList;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        User user = userService.get(id);
        return user;
    }

    @PostMapping("/")
    public ResponseEntity addUser(@RequestBody User user) {
        try {
            userService.add(user);
            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e) {
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/")
    public ResponseEntity updateUser(@RequestBody User user) {
        try {
            userService.update(user);
            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e) {
            return ResponseEntity.ok(HttpStatus.NOT_MODIFIED);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteUser(@PathVariable("id") Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e) {
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }
    }
}
