package com.ewp.crm.controllers;

import com.ewp.crm.component.util.VKUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@PreAuthorize("hasAuthority('ADMIN')")
@Controller
public class VkController {
    private VKUtil vkUtil;

    @Autowired
    public VkController(VKUtil vkUtil) {
        this.vkUtil = vkUtil;
    }

    @RequestMapping(value = "/vk-auth", method = RequestMethod.GET)
    public String vkAuthPage() {
        String uri = vkUtil.receivingTokenUri();
        return "redirect:" + uri;
    }

    @RequestMapping(value = "/vk-auth", method = RequestMethod.POST)
    public String vkGetAccessToken(@RequestParam("token") String token) {
        vkUtil.setApplicationToken(token);
        return "redirect:/client";
    }

    @RequestMapping(value = "/vk-create-group", method = RequestMethod.GET)
    public String vkCreateTargetingGroup(@RequestParam("token") String token) {
        String uri = vkUtil.createGroup(token);
        return "redirect:" + uri;
    }

    @RequestMapping(value = "/vk-add-users", method = RequestMethod.GET)
    public String vkAddUsers(@RequestParam("token") String token) {
        String list = "220499433, 320803898";
        String uri = vkUtil.addUsers(token, list);
        return "redirect:" + uri;
    }
}
