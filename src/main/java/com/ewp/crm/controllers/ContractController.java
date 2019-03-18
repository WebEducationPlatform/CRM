package com.ewp.crm.controllers;

import com.ewp.crm.models.ContractFormData;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/contract")
public class ContractController {

    private final GoogleDriveService googleDriveService;
    private final ClientService clientService;

    @Autowired
    public ContractController(GoogleDriveService googleDriveService, ClientService clientService) {
        this.googleDriveService = googleDriveService;
        this.clientService = clientService;
    }

    @GetMapping
    public ModelAndView completeForm(@RequestParam Long id) {
        ModelAndView model = new ModelAndView("contract");
        model.addObject("clientId", id);
        model.addObject("data", new ContractFormData());
        return model;
    }

    @PostMapping
    public String response(@RequestParam Long id, @ModelAttribute ContractFormData data) {
        clientService.updateClientByIdFromContractForm(id, data);
        Optional<String> contractId = googleDriveService.createContractWithData(data);
        //ИСПРАВИТЬ!
        return contractId.map(fileId -> "redirect:https://docs.google.com/document/d/" + fileId + "/edit?usp=sharing").orElse("redirect:https://vk.com/yogo143");
    }
}
