package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.HrDtoForBoard;
import com.ewp.crm.models.dto.UserRoutesDto;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.UserRoutesService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/hr")
public class HrRestController {
    private static Logger logger = LoggerFactory.getLogger(HrRestController.class);

    private final UserService userService;
    private final RoleService roleService;
    private final UserRoutesService userRoutesService;

    @Autowired
    public HrRestController(UserService userService, RoleService roleService, UserRoutesService userRoutesService) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRoutesService = userRoutesService;
    }

    @GetMapping("/hrlist")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity<List<HrDtoForBoard>> getHrPercentDistribution() {
        return ResponseEntity.ok(userService.getByRole(roleService.getRoleByName("HR"))
                .stream().map(HrDtoForBoard::new)
                .collect(Collectors.toList()));
    }

    @GetMapping("/getuserroutesbytype/{userRoutesType}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity<List<UserRoutesDto>> getHrPercentDistribution(
            @PathVariable String userRoutesType) {
        return ResponseEntity.ok(userRoutesService.getUserByRoleAndUserRoutesType("HR",userRoutesType));
    }

    @PostMapping(value = "/saveroutes")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity saveHrRoutes(@RequestBody List<UserRoutesDto> userRoutesDtoList) {
        userRoutesService.updateUserRoutes(userRoutesDtoList);
        return ResponseEntity.ok(userRoutesDtoList);
    }
}
