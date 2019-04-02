package com.ewp.crm.controllers;

import com.ewp.crm.configs.GoogleAPIConfigImpl;
import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/contract")
public class ContractController {

    private static final Logger logger = LoggerFactory.getLogger(ContractController.class);

    private final ContractService contractService;
    private final ClientService clientService;
    private final ContractSettingService contractSettingService;
    private final ProjectPropertiesService projectPropertiesService;
    private final MailSendService mailSendService;
    private final GoogleAPIConfigImpl googleAPIConfig;


    @Autowired
    public ContractController(ContractService contractService, ClientService clientService, ContractSettingService contractSettingService, ProjectPropertiesService projectPropertiesService, MailSendService mailSendService, GoogleAPIConfigImpl googleAPIConfig) {
        this.contractService = contractService;
        this.clientService = clientService;
        this.contractSettingService = contractSettingService;
        this.projectPropertiesService = projectPropertiesService;
        this.mailSendService = mailSendService;
        this.googleAPIConfig = googleAPIConfig;
    }

    @GetMapping("/{hash}")
    public ModelAndView completeForm(@PathVariable("hash") String hash) {
        if (contractSettingService.existsByHash(hash)) {
            ModelAndView model = new ModelAndView("contract");
            model.addObject("hash", hash);
            model.addObject("data", new ContractDataForm());
            return model;
        }
        return new ModelAndView("404");
    }

    @Transactional
    @PostMapping("/{hash}")
    public String response(@PathVariable("hash") String hash, @ModelAttribute ContractDataForm data) {
        //Есть одноразовая ссылка?
        if (contractSettingService.existsByHash(hash)) {
            //Настройки договора из Бд
            if (contractSettingService.getByHash(hash).isPresent()) {
                ContractSetting setting = contractSettingService.getByHash(hash).get();
                Long clientId = setting.getClientId();
                //Работа с договором и получение ссылки на него
                Optional<String> contractId = contractService.getContractIdByFormDataWithSetting(data, setting);
                if (contractId.isPresent()) {
                    clientService.updateClientFromContractForm(clientService.get(clientId), data, setting.getUser());
                    String docLink = googleAPIConfig.getDocsUri() + contractId.get() + "/edit?usp=sharing";
                    clientService.setContractLink(clientId, docLink);
                    ProjectProperties current = projectPropertiesService.get();
                    if (current.getContractTemplate() != null) {
                        mailSendService.prepareAndSend(clientId, current.getContractTemplate().getTemplateText(), "", null);
                    }
                    contractSettingService.deleteByHash(hash);
                    return "redirect:" + docLink;
                }
                logger.error("Error with getting contract id for client_id = " + clientId);
            }
        }
        return "404";
    }
}
