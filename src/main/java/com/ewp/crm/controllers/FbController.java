package com.ewp.crm.controllers;

import com.ewp.crm.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/*Назаначение контроллера- возвращать id социальной сети вконтакте или facebook по url страницы пользователя*/

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER', 'USER')")
public class FbController {

    private final FacebookService facebookService;

    @Autowired
    public FbController(FacebookService facebookService) {
        this.facebookService = facebookService;

    }

    @GetMapping (value = "/admin/facebook/getFBSocialNetworkId")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity<Long> getSocialNetworkIdFromLink(@RequestParam("socialNetworkLink") String socialNetworkLink) {
        if (facebookService.getFBIdByUrl(socialNetworkLink).isPresent()){
            return new ResponseEntity<>(facebookService.getFBIdByUrl(socialNetworkLink).get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(-1L, HttpStatus.BAD_REQUEST);
        }

    }

}
