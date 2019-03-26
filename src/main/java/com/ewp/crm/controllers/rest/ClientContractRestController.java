package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ContractSetting;
import com.ewp.crm.service.interfaces.ContractSettingService;
import com.ewp.crm.service.interfaces.GoogleTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/contract/rest")
public class ClientContractRestController {
    private static final Logger logger = LoggerFactory.getLogger(ClientContractRestController.class);

    private final ContractSettingService settingService;
    private final GoogleTokenService googleTokenService;

    public ClientContractRestController(ContractSettingService settingService, GoogleTokenService googleTokenService) {
        this.settingService = settingService;
        this.googleTokenService = googleTokenService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity createContractSetting(@RequestBody ContractSetting setting) {
        if (googleTokenService.getToken().isPresent()) {
            settingService.save(setting);
            logger.info("create unique contract link for client id = {}", setting.getClientId());
            return new ResponseEntity(HttpStatus.OK);
        } else return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
