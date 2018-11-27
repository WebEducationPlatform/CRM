package com.ewp.crm.controllers;

import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.models.VkTrackedClub;
import com.ewp.crm.service.interfaces.VKService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.VkTrackedClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER', 'USER')")
public class VkController {

    private final UserService userService;
    private final VKService vkService;
    private final VkTrackedClubService vkTrackedClubService;
    private final ProjectPropertiesService projectPropertiesService;

    private ProjectProperties projectProperties;

    @Autowired
    public VkController(VKService vkService,
                        UserService userService,
                        VkTrackedClubService vkTrackedClubService, ProjectPropertiesService projectPropertiesService) {
        this.vkService = vkService;
        this.userService = userService;
        this.vkTrackedClubService = vkTrackedClubService;
        this.projectPropertiesService = projectPropertiesService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    @GetMapping(value = "/admin/vkontakte/trackedclub")
    public ModelAndView trackingGroupInfo() {
        ModelAndView modelAndView = new ModelAndView("vk-trackedclub-info");
        modelAndView.addObject("vkTrackedClub", vkTrackedClubService.getAll());
        modelAndView.addObject("newVkTrackedClub", new VkTrackedClub());
        return modelAndView;
    }

    @GetMapping(value = "/vk-auth")
    public String vkAuthPage() {
        String uri = vkService.receivingTokenUri();
        return "redirect:" + uri;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    @PostMapping(value = "/vk-auth")
    public String vkGetAccessToken(@RequestParam("token") String token) {
        String applicationToken = vkService.replaceApplicationTokenFromUri(token);
        if ((projectProperties = projectPropertiesService.get()) == null) {
            projectProperties = new ProjectProperties();
        }
        projectProperties.setTechnicalAccountToken(applicationToken);
        projectPropertiesService.saveAndFlash(new ProjectProperties(applicationToken));
        return "redirect:/client";
    }

}
