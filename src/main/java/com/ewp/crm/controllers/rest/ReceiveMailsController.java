package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.dto.MailDto;
import com.ewp.crm.service.interfaces.MailReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReceiveMailsController {

    private MailReceiverService mailReceiverService;

    @Autowired
    public ReceiveMailsController(MailReceiverService mailReceiverService) {
        this.mailReceiverService = mailReceiverService;
    }

    @GetMapping(value = "/mail/checkForNewEmails")
    public ResponseEntity<List<Long>> checkAllNewEmails(){
        List<Long> userList = mailReceiverService.checkMessagesInGMailInbox();
        return ResponseEntity.ok(userList);
    }

    @PostMapping(value = "/mail/getNewEmails")
    public ResponseEntity<List<MailDto>> getAllUnreadEmailsFor(@RequestParam (name = "id") Long id){
        List<MailDto> mailList = mailReceiverService.getAllUnreadEmailsFor(id);
        return ResponseEntity.ok(mailList);
    }
}
