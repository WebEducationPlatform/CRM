package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.service.StatusService;
import com.ewp.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/user")
public class RestUserController {
    @Autowired
    UserService userService;

    @Autowired
    StatusService statusService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<User> getAll(){
        return userService.getAllUsers();
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User getUserByID(@PathVariable Long id){
        return userService.getUserByID(id);
    }

    @RequestMapping(value = "/update/{id}",method = RequestMethod.POST)
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user){
        User currentUser = userService.getUserByID(id);
        if (currentUser == null) {
            System.out.println("User with id " + id + " not found");
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
        user.setId(currentUser.getId());
        userService.updateUser(user);
        return new ResponseEntity<User>(HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}",method = RequestMethod.POST)
    public ResponseEntity<User> deleteUser(@PathVariable Long id){
        User currentUser = userService.getUserByID(id);
        if (currentUser == null) {
            System.out.println("User with id " + id + " not found");
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(currentUser);
        return new ResponseEntity<User>(HttpStatus.OK);
    }

    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    public ResponseEntity<String> addUser(@RequestBody User user){
        User currentUser = userService.getUserByEmail(user.getEmail());
        if (currentUser != null) {
            return new ResponseEntity<>("User already exists",HttpStatus.BAD_REQUEST);
        }
        Status stat;
        if((stat =statusService.getStatusByName(user.getStatus().getName()))!=null){
            user.setStatus(stat);
        }
        userService.addUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
