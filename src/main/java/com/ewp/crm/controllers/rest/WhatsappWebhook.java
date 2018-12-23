package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.whatsapp.whatsappDTO.WhatsappAcknowledgement;
import com.ewp.crm.models.whatsapp.whatsappDTO.WhatsappAcknowledgementDTO;
import com.ewp.crm.models.whatsapp.WhatsappMessage;
import com.ewp.crm.repository.interfaces.WhatsappMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/whatsapp")
public class WhatsappWebhook {
    private final WhatsappMessageRepository whatsappMessageRepository;

    @Autowired
    public WhatsappWebhook(WhatsappMessageRepository whatsappMessageRepository) {
        this.whatsappMessageRepository = whatsappMessageRepository;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> updateUser(@RequestBody WhatsappAcknowledgementDTO dto) {
        List<WhatsappMessage> whatsappMessages = dto.getMessages();
        List<WhatsappAcknowledgement> receipts = dto.getAck();
        if (whatsappMessages != null) {
            List<WhatsappMessage> collect = whatsappMessages.stream()
                    .filter(x -> x.getChatId().matches("\\d*@c.*")&&x.getBody().length()>0&&x.getType().equals("chat"))
                    .peek(x->x.setChatId(x.getChatId().replaceAll("\\D","")))
                    .collect(Collectors.toList());
            //to do добавить логику создания нового пользователя
            whatsappMessageRepository.saveAll(collect);
        }
        if (receipts != null) {

            System.out.println(receipts.size());
            for (WhatsappAcknowledgement whatsappAcknowledgement : receipts
            ) {
                String id = whatsappAcknowledgement.getId();
                WhatsappMessage whatsappMessage = whatsappMessageRepository.findById(id).orElse(null);
                if (whatsappMessage!=null&&whatsappAcknowledgement.getStatus().getPriority()>2){
                    whatsappMessage.setRead(true);
                    whatsappMessageRepository.save(whatsappMessage);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

}
