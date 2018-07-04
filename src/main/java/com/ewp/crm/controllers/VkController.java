package com.ewp.crm.controllers;

import com.ewp.crm.component.util.VKUtil;
import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER', 'USER')")
@Controller
public class VkController {

    private static Logger logger = LoggerFactory.getLogger(VkController.class);

    private final VKUtil vkUtil;

    private final UserService userService;

    @Autowired
    public VkController(VKUtil vkUtil, UserService userService) {
        this.vkUtil = vkUtil;
        this.userService = userService;
    }

    @RequestMapping(value = "/vk-auth", method = RequestMethod.GET)
    public String vkAuthPage() {
        String uri = vkUtil.receivingTokenUri();
        return "redirect:" + uri;
    }

    @RequestMapping(value = "/vk-auth", method = RequestMethod.POST)
    public String vkGetAccessToken(@RequestParam("token") String token) {
        User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.get(userFromSession.getId());
        String applicationToken = vkUtil.replaceApplicationTokenFromUri(token);
        user.setVk_token(applicationToken);
        userService.update(user);
        if (user.getAuthorities().contains(new Role("OWNER"))){
            vkUtil.setApplicationToken(applicationToken);
        }
        logger.info("Token of " + user.getFullName() + " is: " + vkUtil.replaceApplicationTokenFromUri(token));
        return "redirect:/client";
    }
}
