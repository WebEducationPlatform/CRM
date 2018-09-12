package com.ewp.crm.controllers;

import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import com.ewp.crm.models.VkTrackedClub;
import com.ewp.crm.service.impl.VKService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.VkTrackedClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER', 'USER')")
public class VkController {

    private final UserService userService;
    private final VKService vkService;
    private final VkTrackedClubService vkTrackedClubService;

    @Autowired
    public VkController(VKService vkService,
                        UserService userService,
                        VkTrackedClubService vkTrackedClubService) {
        this.vkService = vkService;
        this.userService = userService;
        this.vkTrackedClubService = vkTrackedClubService;
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

    @PostMapping(value = "/vk-auth")
    public String vkGetAccessToken(@RequestParam("token") String token,
                                   @AuthenticationPrincipal User userFromSession) {
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
