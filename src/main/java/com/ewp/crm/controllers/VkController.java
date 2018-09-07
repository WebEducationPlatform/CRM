package com.ewp.crm.controllers;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import com.ewp.crm.models.VkTrackedClub;
import com.ewp.crm.service.impl.VKService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.VkTrackedClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER', 'USER')")
@Controller
public class VkController {

    private final UserService userService;
    private VKService vkService;
    private VkTrackedClubService vkTrackedClubService;

    @Autowired
    public VkController(VKService vkService, UserService userService, VkTrackedClubService vkTrackedClubService) {
        this.vkService = vkService;
        this.userService = userService;
        this.vkTrackedClubService = vkTrackedClubService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    @RequestMapping(value = "/admin/vkontakte/trackedclub", method = RequestMethod.GET)
    public ModelAndView trackingGroupInfo() {
        ModelAndView modelAndView = new ModelAndView("vk-trackedclub-info");
        modelAndView.addObject("vkTrackedClub", vkTrackedClubService.getAll());
        modelAndView.addObject("newVkTrackedClub", new VkTrackedClub());
        return modelAndView;
    }

    @RequestMapping(value = "/vk-auth", method = RequestMethod.GET)
    public String vkAuthPage() {
        String uri = vkService.receivingTokenUri();
        return "redirect:" + uri;
    }

    @RequestMapping(value = "/vk-auth", method = RequestMethod.POST)
    public String vkGetAccessToken(@RequestParam("token") String token) {
        User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.get(userFromSession.getId());
        String applicationToken = vkService.replaceApplicationTokenFromUri(token);
        user.setVkToken(applicationToken);
        userService.update(user);
        if (user.getAuthorities().contains(new Role("OWNER"))) {
            vkService.setApplicationToken(applicationToken);
        }
        return "redirect:/client";
    }
}
