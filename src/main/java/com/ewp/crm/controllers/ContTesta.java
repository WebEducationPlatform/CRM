package com.ewp.crm.controllers;

import com.ewp.crm.models.dto.MentorDtoForMentorsPage;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class ContTesta {
    @Autowired
    UserService userService;
    @RequestMapping("test")
    public String test(){
        StringBuilder stringBuilder = new StringBuilder();
        List<MentorDtoForMentorsPage> allMentors = userService.getAllMentors();


        allMentors.forEach(mentorDtoForMentorsPage -> stringBuilder.append(mentorDtoForMentorsPage.getUser_Id()));
        return stringBuilder.toString();
    }
}
