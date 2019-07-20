package com.ewp.crm.controllers;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.dto.MentorDtoForMentorsPage;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasAnyAuthority('OWNER','ADMIN','MENTOR')")
@RequestMapping("/mentors")
@PropertySource({"file:./slackbot.properties", "file:./mentors.properties"})
public class MentorsController {

    private static Logger logger = LoggerFactory.getLogger(MentorsController.class);

    private final StatusService statusService;
    private final UserService userService;
    private final MessageTemplateService messageTemplateService;
    private final ProjectPropertiesService propertiesService;
    private final StudentStatusService studentStatus;
    private final RoleService roleService;


    @Autowired
    public MentorsController(StatusService statusService,
                             UserService userService,
                             MessageTemplateService MessageTemplateService,
                             ProjectPropertiesService propertiesService,
                             StudentStatusService studentStatus,
                             RoleService roleService) {
        this.statusService = statusService;
        this.userService = userService;
        this.messageTemplateService = MessageTemplateService;
        this.propertiesService = propertiesService;
        this.studentStatus = studentStatus;
        this.roleService = roleService;
    }

    @Value("${slackbot.domain}")
    private String slackBotDomain;
    @Value("${mentor.max.students}")
    private String maxStudents;

    @GetMapping
    public ModelAndView showMentorsWithThearStudents() {
        List<MentorDtoForMentorsPage.MentorDto> mentorDtos = new ArrayList<>();
        userService.getAllMentors().forEach(m -> mentorDtos.add(new MentorDtoForMentorsPage.MentorDto(m.getUser_Id(), m.getEmail())));
        ModelAndView modelAndView = new ModelAndView("mentors-with-students-table");
        modelAndView.addObject("slackBotDomain", slackBotDomain);
        modelAndView.addObject("maxStudents", maxStudents);
        modelAndView.addObject("mentors", mentorDtos);
        modelAndView.addObject("studentStatuses", studentStatus.getAll());
        modelAndView.addObject("statuses", statusService.getAll());
        modelAndView.addObject("projectProperties", propertiesService.get());
        modelAndView.addObject("users", userService.getAll());
        modelAndView.addObject("socialNetworkTypes", new SocialProfile().getAllSocialNetworkTypes());
        modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
        return modelAndView;
    }
}
