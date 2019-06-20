package com.ewp.crm.controllers;

import com.ewp.crm.models.ClientOtherInformation;
import com.ewp.crm.models.OtherInformationInputValues;
import com.ewp.crm.service.interfaces.ClientOtherInformationService;
import com.ewp.crm.service.interfaces.OtherInformationLinkDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

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

    @PostMapping(value = "/link")
    public String createLinkToClient(@RequestBody OtherInformationInputValues data) {
        if (otherInformationLinkDataService.existsByHash(data.getHash())) {
            Long clientId = otherInformationLinkDataService.getByHash(data.getHash()).get().getClient().getId();
            for (Map<String, String> value : data.getOtherInformationInputValues()) {
                String name = value.get("name");
                ClientOtherInformation clientOtherInformation = clientOtherInformationService.getClientOtherInformationByNameAndClientId(name, null);
                ClientOtherInformation newClientOtherInformationToOurClient = new ClientOtherInformation(name, clientOtherInformation.getTypeField());
                if (clientOtherInformation.getTypeField().equals("CHECKBOX")) {
                    String booleanValue = value.get("value");
                    if (booleanValue.equals("true")) {
                        newClientOtherInformationToOurClient.setCheckboxValue(true);
                    } else {
                        newClientOtherInformationToOurClient.setCheckboxValue(false);
                    }
                } else {
                    String textValue = value.get("value");
                    newClientOtherInformationToOurClient.setTextValue(textValue);
                }
                newClientOtherInformationToOurClient.setClientId(clientId);
                clientOtherInformationService.save(newClientOtherInformationToOurClient);
            }
        }
        return "404";
    }
}