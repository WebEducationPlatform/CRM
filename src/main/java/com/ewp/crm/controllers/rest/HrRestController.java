package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/hr")
public class HrRestController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public HrRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/getuserroutesbytype/{userRoutesType}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity<List<User>> getHrPercentDistribution(
            @PathVariable String userRoutesType, @AuthenticationPrincipal User userFromSession) {

        return ResponseEntity.ok(userService.getByRole(roleService.getRoleByName("HR")));
    }
}
