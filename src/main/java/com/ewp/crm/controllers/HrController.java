package com.ewp.crm.controllers;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.StudentStatusService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/hr")
@PreAuthorize("hasAnyAuthority('OWNER','HR')")
@PropertySource("file:./slackbot.properties")
public class HrController {
    private static Logger logger = LoggerFactory.getLogger(HrController.class);

    private final StatusService statusService;
    private final ClientService clientService;
    private final UserService userService;
    private final MessageTemplateService messageTemplateService;
    private final ProjectPropertiesService propertiesService;
    private final StudentStatusService studentStatus;
    private final RoleService roleService;


    @Value("${slackbot.ip}")
    private String slackBotIp;
    @Value("${slackbot.port}")
    private String slackBotPort;

    @Autowired
    public HrController(StatusService statusService,
                        ClientService clientService,
                        UserService userService,
                        MessageTemplateService MessageTemplateService,
                        ProjectPropertiesService propertiesService,
                        StudentStatusService studentStatus,
                        RoleService roleService) {
        this.statusService = statusService;
        this.clientService = clientService;
        this.userService = userService;
        this.messageTemplateService = MessageTemplateService;
        this.propertiesService = propertiesService;
        this.studentStatus = studentStatus;
        this.roleService = roleService;
    }

    @GetMapping("/students")
    public ModelAndView showAllStudents() {
        ModelAndView modelAndView = new ModelAndView("main-client-table-hr");
        SocialProfile socialProfile = new SocialProfile();
        modelAndView.addObject("allClients", clientService.getAllClientsByPage(PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "dateOfRegistration"))));
        modelAndView.addObject("slackBotIp", slackBotIp);
        modelAndView.addObject("slackBotPort", slackBotPort);
        modelAndView.addObject("statuses", statusService.getAll());
        modelAndView.addObject("projectProperties", propertiesService.get());
        modelAndView.addObject("users", userService.getAll());
        modelAndView.addObject("socialNetworkTypes", socialProfile.getAllSocialNetworkTypes());
        modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
        modelAndView.addObject("studentStatuses", studentStatus.getAll());
        return modelAndView;
    }

    @GetMapping("/managers")
    public ModelAndView showAllManagers() {
        ModelAndView modelAndView = new ModelAndView("hr-table");
        modelAndView.addObject("hrManagers", userService.getAll().stream().filter(x -> x.getRole().contains(roleService.getRoleByName("HR"))).collect(Collectors.toList()));
        modelAndView.addObject("emailTmpl", messageTemplateService.getAll());
        return modelAndView;
    }

}
