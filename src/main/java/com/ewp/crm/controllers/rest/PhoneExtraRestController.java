package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.PhoneExtra;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.PhoneExtraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/phonextra")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class PhoneExtraRestController {

    private static Logger logger = LoggerFactory.getLogger(PhoneExtraRestController.class);

    private final ClientService clientService;
    private final PhoneExtraService phoneExtraService;

    @Autowired
    public PhoneExtraRestController(ClientService clientService,
                                    PhoneExtraService phoneExtraService) {
        this.clientService = clientService;
        this.phoneExtraService = phoneExtraService;
    }

    @GetMapping(value = "/getByClient/{clientId}")
    public ResponseEntity<List<PhoneExtra>> getPhoneExtrasByClient(@PathVariable Long clientId) {
        Client client = clientService.get(clientId);
        List<PhoneExtra> phoneExtras = phoneExtraService.getAllPhonesExtraByClient(client);
        return ResponseEntity.ok(phoneExtras);
    }

    @GetMapping(value = "/delete/{phoneExtraId}")
    public ResponseEntity delPhoneExtras(@PathVariable Long phoneExtraId) {
        if (phoneExtraService.get(phoneExtraId) == null) {
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        } else {
            phoneExtraService.delete(phoneExtraId);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = "/add")
    public ResponseEntity<PhoneExtra> addPhoneExtra(@RequestParam(name = "clientId") Long clientId,
                                              @RequestParam(name = "phoneExtra") String phone) {
        Client client = clientService.get(clientId);
        if (client == null) {
            logger.error("Can`t add additional phone, client with id {} not found", clientId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        PhoneExtra phoneExtra = new PhoneExtra(phone, client);
        phoneExtraService.add(phoneExtra);
        return ResponseEntity.status(HttpStatus.CREATED).body(phoneExtra);
    }

    @PostMapping(value = "/edit")
    public ResponseEntity<PhoneExtra> editPhoneExtra(@RequestParam(name = "id") Long phoneExtraId,
                                                    @RequestParam(name = "phoneExtra") String phone) {
        PhoneExtra phoneExtra = phoneExtraService.get(phoneExtraId);
        if (phoneExtra == null) {
            logger.error("Can`t edit additional phone, phoneExtra with id {} not found", phone);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            phoneExtra.setPhoneExtra(phone);
            phoneExtraService.update(phoneExtra);
            return ResponseEntity.status(HttpStatus.OK).body(phoneExtra);
        }
    }

}
