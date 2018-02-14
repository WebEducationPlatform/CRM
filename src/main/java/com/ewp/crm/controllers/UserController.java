package com.ewp.crm.controllers;

import com.ewp.crm.models.User;
import com.ewp.crm.service.UserService;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    //TODO Определить везде view

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getall() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("users",userService.getAllUsers());
        return modelAndView;
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public void addUser(User user) {
        userService.addUser(user);
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public void updateUser(User user) {
        userService.updateUser(user);
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public void deleteUser(User user) {
        userService.deleteUser(user);
    }
}
