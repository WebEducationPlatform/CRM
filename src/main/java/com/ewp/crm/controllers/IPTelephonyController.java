package com.ewp.crm.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/calls")
public class IPTelephonyController {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public String getPage() {
        return "calls";
    }
}