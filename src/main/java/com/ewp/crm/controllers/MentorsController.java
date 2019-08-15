package com.ewp.crm.controllers;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.dto.ClientDto;
import com.ewp.crm.models.dto.MentorDtoForMentorsPage;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.StudentStatusService;
import com.ewp.crm.service.interfaces.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


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
    private final ClientService clientService;


    @Autowired
    public MentorsController(StatusService statusService,
                             UserService userService,
                             MessageTemplateService MessageTemplateService,
                             ProjectPropertiesService propertiesService,
                             StudentStatusService studentStatus,
                             RoleService roleService, ClientService clientService) {
        this.statusService = statusService;
        this.userService = userService;
        this.messageTemplateService = MessageTemplateService;
        this.propertiesService = propertiesService;
        this.studentStatus = studentStatus;
        this.roleService = roleService;
        this.clientService = clientService;
    }

    @Value("${slackbot.domain}")
    private String slackBotDomain;
    @Value("${mentor.max.students}")
    private String maxStudents;
    @Value("${slackbot.access.protocol}")
    private String slackBotAccessProtocol;

    @GetMapping
    public ModelAndView showMentorsWithTheirStudents() {
        String url = slackBotAccessProtocol + slackBotDomain + "/mentor/all/students";
        List<MentorDtoForMentorsPage.MentorDto> mentorDtos = new ArrayList<>();
        userService.getAllMentors().forEach(m -> mentorDtos.add(new MentorDtoForMentorsPage.MentorDto(m.getUser_Id(), m.getEmail())));
        String mentorsFromBotJson = new RestTemplate().postForObject(url, mentorDtos, String.class);
        Map<String, ClientDto> studentsDto = getStudentsFromBotDtoMentors(mentorsFromBotJson);


        ModelAndView modelAndView = new ModelAndView("mentors-with-students-table");
        modelAndView.addObject("studentsDto", studentsDto);
        modelAndView.addObject("mentorsFromBotJson", Objects.requireNonNull(mentorsFromBotJson));
        modelAndView.addObject("slackBotDomain", slackBotDomain);
        modelAndView.addObject("slackBotAccessProtocol", slackBotAccessProtocol);
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
    private Map<String, ClientDto> getStudentsFromBotDtoMentors(String mentorsFromBotJson) {
        List<String> clientsEmails = new ArrayList<>();
        try {
            Iterator<JsonNode> mentorsWithTheirStudentsJsonNodeIterable = new ObjectMapper().readTree(mentorsFromBotJson).elements();
            mentorsWithTheirStudentsJsonNodeIterable.forEachRemaining(mentorIdStudentsEmails -> {
                        mentorIdStudentsEmails.get("emailsStudents").elements().forEachRemaining(email -> {
                            clientsEmails.add(email.asText().toLowerCase());
                        });
                        mentorIdStudentsEmails.get("emailsTrialStudents").elements().forEachRemaining(email -> {
                            clientsEmails.add(email.asText().toLowerCase());
                        });
                        mentorIdStudentsEmails.get("emailsLostStudents").elements().forEachRemaining(email -> {
                            clientsEmails.add(email.asText().toLowerCase());
                        });
                        mentorIdStudentsEmails.get("emailsLostTrialStudents").elements().forEachRemaining(email -> {
                            clientsEmails.add(email.asText().toLowerCase());
                        });
                    }
            );
        } catch (IOException e) {
            logger.error(e.getMessage());
        }



        Map<String, ClientDto> emailStudentsMap = new HashMap<>();
        List<ClientDto.ClientTransformer> clientsDtoByEmails = clientService.getClientsDtoByEmails(clientsEmails);
        List<ClientDto> clients = new ArrayList<>();
        clientsDtoByEmails.forEach(clientTransformer ->
                clients.add(new ClientDto(
                        clientTransformer.getClient_id(), clientTransformer.getFirst_name(), clientTransformer.getLast_name(),
                        "", clientTransformer.getClient_email())));
        clients.forEach(client -> emailStudentsMap.put(client.getEmail().toLowerCase(), client));

        return emailStudentsMap;
    }
}
