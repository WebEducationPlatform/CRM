package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/properties")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class ProjectPropertiesRestController {

    private final ProjectPropertiesService projectPropertiesService;

    @Autowired
    public ProjectPropertiesRestController(ProjectPropertiesService projectPropertiesService) {
        this.projectPropertiesService = projectPropertiesService;
    }

    @GetMapping("/status")
    public ResponseEntity<Long> getStatus() {
        ProjectProperties projectProperties = projectPropertiesService.get();
        Long status = -1L;
        if (projectProperties != null) {
            status = projectProperties.getDefaultStatusId();
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
