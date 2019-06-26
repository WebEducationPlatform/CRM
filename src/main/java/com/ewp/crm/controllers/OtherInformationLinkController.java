package com.ewp.crm.controllers;

import com.ewp.crm.service.interfaces.ClientOtherInformationService;
import com.ewp.crm.service.interfaces.OtherInformationLinkDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/information")
public class OtherInformationLinkController {
    private OtherInformationLinkDataService otherInformationLinkDataService;
    private ClientOtherInformationService clientOtherInformationService;

    @Autowired
    public OtherInformationLinkController(OtherInformationLinkDataService otherInformationLinkDataService, ClientOtherInformationService clientOtherInformationService) {
        this.otherInformationLinkDataService = otherInformationLinkDataService;
        this.clientOtherInformationService = clientOtherInformationService;
    }

    @GetMapping("/{hash}")
    public ModelAndView clientOtherInformationNewPage(@PathVariable("hash") String hash) {
        if (otherInformationLinkDataService.existsByHash(hash)) {
            ModelAndView model = new ModelAndView("client-other-information");
            model.addObject("hash", hash);
            model.addObject("data", clientOtherInformationService.getAllClientOtherInformaionById(null));
            return model;
        }
        return new ModelAndView("404");
    }

    @GetMapping("/thanks")
    public ModelAndView thanksToClient(){
        return new ModelAndView("answer-to-client");
    }
}