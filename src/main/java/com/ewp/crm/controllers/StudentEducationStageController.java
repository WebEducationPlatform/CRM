package com.ewp.crm.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNEN', 'HR', 'MENTOR')")
@RequestMapping(value = "/educationstages")
public class StudentEducationStageController {

}
