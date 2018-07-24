package com.ewp.crm.controllers.rest;

import com.ewp.crm.service.interfaces.FacebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FbController {

    @Autowired
    private FacebookService facebookService;

    @RequestMapping(value = "/test/rest", method = RequestMethod.GET)
    public ResponseEntity<String> testFb(@RequestParam String link){
        facebookService.refactorAndValidFbLink(link);
        return ResponseEntity.ok().body("ok");
    }
}
