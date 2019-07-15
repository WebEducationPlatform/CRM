package com.ewp.crm.controllers;

import com.ewp.crm.configs.GoogleAPIConfigImpl;
import com.ewp.crm.models.*;
import com.ewp.crm.repository.interfaces.ClientsContractLinkRepository;
import com.ewp.crm.repository.interfaces.ContractLinkDataRepository;
import com.ewp.crm.service.interfaces.*;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
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
    private final ContractLinkDataRepository contractLinkRepository;
    private final ClientsContractLinkRepository clientsContractLinkRepository;
    private Environment environment;

    @Autowired
    public ContractController(ContractService contractService,
                              ClientService clientService,
                              ContractSettingService contractSettingService,
                              ProjectPropertiesService projectPropertiesService,
                              MailSendService mailSendService,
                              GoogleAPIConfigImpl googleAPIConfig,
                              ContractLinkDataRepository contractLinkRepository,
                              ClientsContractLinkRepository clientsContractLinkRepository,
                              Environment environment) {
        this.contractService = contractService;
        this.clientService = clientService;
        this.contractSettingService = contractSettingService;
        this.projectPropertiesService = projectPropertiesService;
        this.mailSendService = mailSendService;
        this.googleAPIConfig = googleAPIConfig;
        this.contractLinkRepository =contractLinkRepository;
        this.clientsContractLinkRepository = clientsContractLinkRepository;
        this.environment = environment;
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
                Map<String,String> contractDataMap = contractService.getContractIdByFormDataWithSetting(data, setting);
                if (!contractDataMap.isEmpty()) {
                    clientService.updateClientFromContractForm(clientService.get(clientId), data, setting.getUser());
                    String docLink;
                    String googleDocUrl = googleAPIConfig.getDocsUri();
                    String googleContractId = contractDataMap.get("contractId");
                    if (setting.isStamp()) {
                        docLink = googleAPIConfig.getViewUri() + googleDocUrl + googleContractId + "/export?format=pdf";
                    } else {
                        docLink = googleDocUrl + googleContractId + "/edit?usp=sharing";
                    }
                    clientService.setContractLink(clientId, docLink, contractDataMap.get("contractName"));
                    ProjectProperties current = projectPropertiesService.get();
                    if (current.getContractTemplate() != null) {
                        String contractTheme = environment.getRequiredProperty("contract.email.theme");
                        mailSendService.prepareAndSend(clientId, current.getContractTemplate().getTemplateText(), StringUtils.EMPTY, null, contractTheme);
                    }
                    contractSettingService.deleteByHash(hash);
                    return "redirect:" + docLink;
                }
                logger.error("Error with getting contract id for client with id {}", clientId);
            }
        }
        return "404";
    }

    @GetMapping("/updateLink")
    public ResponseEntity<String> updateContractLink(@RequestParam Long id) {
        Client client = clientService.get(id);
        //если обновилась отправить письмо
        if (contractService.updateContractLink(client.getContractLinkData())) {
            String contractTheme = environment.getRequiredProperty("contract.email.theme");
            mailSendService.prepareAndSend(id, projectPropertiesService.getOrCreate().getContractTemplate().getTemplateText(), StringUtils.EMPTY, null, contractTheme);
        }
        return new ResponseEntity<>(client.getContractLinkData().getContractLink(), HttpStatus.OK);
    }

    @DeleteMapping("/deleteContract")
    public ResponseEntity deleteContract(@RequestParam Long id){
        Optional<ContractLinkData> contractLinkData = clientsContractLinkRepository.getByClientId(id);
        if (contractLinkData.isPresent()){
            contractService.deleteContractFromGoogleDrive(contractLinkData.get().getContractLink());
            contractLinkRepository.deleteContactLinkByClientId(id);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
