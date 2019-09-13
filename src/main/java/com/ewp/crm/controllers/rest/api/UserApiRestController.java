package com.ewp.crm.controllers.rest.api;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/api/user/")
public class UserApiRestController {

    private static Logger logger = LoggerFactory.getLogger(UserApiRestController.class);

    private final UserService userService;
    private final ObjectMapper mapper;
    private final ClientService clientService;
    private final CommentService commentService;
    private final SMSInfoService smsInfoService;

    @Autowired
    public UserApiRestController(UserService userService,
                                 ObjectMapper mapper,
                                 ClientService clientService,
                                 CommentService commentService,
                                 SMSInfoService smsInfoService) {
        this.userService = userService;
        this.mapper = mapper;
        this.clientService = clientService;
        this.commentService = commentService;
        this.smsInfoService = smsInfoService;
    }

    @GetMapping("/")
    public List<User> getAllUsers() {
        List<User> userList = userService.getAll();
        logger.info("All users are received");
        return userList;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        User user = userService.get(id);
        logger.info("The user is returned: " + id);
        return user;
    }

    @PostMapping("/")
    public ResponseEntity addUser(@RequestBody User user) {
        try {
            userService.add(user);
            logger.info("The user has been added: " + user.getFullName());
            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e) {
            logger.error("An error occurred while adding a user: " + e.getMessage());
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/")
    public ResponseEntity updateUser(@RequestBody User user) {
        try {
            userService.update(user);
            logger.info("User changes were successful: " + user.getFullName());
            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e) {
            logger.error("An error occurred while changing the user: " + e.getMessage());
            return ResponseEntity.ok(HttpStatus.NOT_MODIFIED);
        }
    }

    @DeleteMapping("/")
    public ResponseEntity deleteUserTransferClients(@RequestParam("id_delete") Long id,
                                                    @RequestParam("id_transfer") Long id_transfer) {
        try {
            User deletedUser = userService.get(id);
            User receiver = userService.get(id_transfer);
            clientService.transferClientsBetweenOwners(deletedUser, receiver);
            commentService.deleteAllCommentsByUserId(id);
            smsInfoService.deleteAllSMSByUserId(id);
            userService.delete(id);
            logger.info("User deletion was successful: " + deletedUser.getFullName());
            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e) {
            logger.error("An error occurred while deleting a user: " + e.getMessage());
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteUser(@PathVariable("id") Long id) {
        try {
            userService.delete(id);
            logger.info("User deletion was successful: " + id);
            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e) {
            logger.error("An error occurred while deleting a user: " + e.getMessage());
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }
    }
}
