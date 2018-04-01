package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialNetwork;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
public class UserRestController {

    private static Logger logger = LoggerFactory.getLogger(RestClientController.class);

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/rest/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @RequestMapping(value = "/admin/rest/user/update", method = RequestMethod.POST)
    public ResponseEntity updateClient(@RequestBody User user) {
        User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.update(user);
        logger.info("{} has updated user: id {}, email {}", currentAdmin.getFullName(), user.getId(), user.getEmail());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/rest/user/delete", method = RequestMethod.POST)
    public ResponseEntity deleteUser(@RequestParam(name = "deleteId") Long deleteId) {
        User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.get(deleteId);
        for(Client ownedClient: currentUser.getOwnedClients()) {
            ownedClient.setOwnerUser(null);
        }
        userService.delete(deleteId);
        logger.info("{} has  deleted user  with id {}, email {}",  currentAdmin.getFullName(), deleteId, currentUser.getEmail());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = {"/admin/rest/user/update/photo"}, method = RequestMethod.POST)
    public ResponseEntity addAvatar(@RequestParam("0") MultipartFile file, @RequestParam("id") Long id) {
        User user = userService.get(id);
        userService.addPhoto(file, user);
	    return ResponseEntity.ok().body("{\"msg\":\"Сохранено\"}");
    }

    @RequestMapping(value = {"/user/socialMarkers"}, method = RequestMethod.GET)
    public ResponseEntity<SocialNetwork.SocialMarker[]> getSocialMarkers() {
        return ResponseEntity.ok(SocialNetwork.SocialMarker.values());
    }

    @RequestMapping(value = "/admin/rest/user/add", method = RequestMethod.POST)
    public ResponseEntity addClient(@RequestBody User user) {
        User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.update(user);
        logger.info("{} has added user: email {}", currentAdmin.getFullName(), user.getEmail());
        return ResponseEntity.ok().body(userService.getUserByEmail(user.getEmail()).getId());
    }
}
