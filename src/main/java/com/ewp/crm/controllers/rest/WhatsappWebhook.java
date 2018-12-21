package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.whatsapp.whatsappDTO.WhatsappAcknowledgement;
import com.ewp.crm.models.whatsapp.whatsappDTO.WhatsappAcknowledgementDTO;
import com.ewp.crm.models.whatsapp.WhatsappMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/whatsapp")
public class WhatsappWebhook {
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> updateUser(@RequestBody WhatsappAcknowledgementDTO dto) {
        List<WhatsappMessage> whatsappMessages = dto.getWhatsappMessages();
        List<WhatsappAcknowledgement> receipts = dto.getAck();
        if (whatsappMessages != null) {
            System.out.println(whatsappMessages.size());
            for (WhatsappMessage m : whatsappMessages
            ) {
                System.out.println(m);
            }
        }if (receipts!=null){

            System.out.println(receipts.size());
            for (WhatsappAcknowledgement whatsappAcknowledgement : receipts
            ) {
                System.out.println(whatsappAcknowledgement.getStatus().getPriority());
                System.out.println(whatsappAcknowledgement);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

}
