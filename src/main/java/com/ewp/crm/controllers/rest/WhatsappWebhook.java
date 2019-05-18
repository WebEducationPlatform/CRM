package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.whatsapp.WhatsappMessage;
import com.ewp.crm.models.whatsapp.whatsappDTO.WhatsappAcknowledgement;
import com.ewp.crm.models.whatsapp.whatsappDTO.WhatsappAcknowledgementDTO;
import com.ewp.crm.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/whatsapp")
public class WhatsappWebhook {

    private final ClientService clientService;
    private final SendNotificationService sendNotificationService;
    private final WhatsappMessageService whatsappMessageService;
    private final StatusService statusService;
    private final SocialProfileTypeService socialProfileTypeService;
    private final SocialProfileService socialProfileService;
    private final ClientHistoryService clientHistoryService;

    @Autowired
    public WhatsappWebhook(ClientService clientService, SendNotificationService sendNotificationService, WhatsappMessageService whatsappMessageService, StatusService statusService, SocialProfileTypeService socialProfileTypeService, SocialProfileService socialProfileService, ClientHistoryService clientHistoryService) {
        this.clientService = clientService;
        this.sendNotificationService = sendNotificationService;
        this.whatsappMessageService = whatsappMessageService;
        this.statusService = statusService;
        this.socialProfileTypeService = socialProfileTypeService;
        this.socialProfileService = socialProfileService;
        this.clientHistoryService = clientHistoryService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public HttpStatus updateUser(@RequestBody WhatsappAcknowledgementDTO dto) {

        List<WhatsappMessage> whatsappMessages = dto.getMessages();
        List<WhatsappAcknowledgement> receipts = dto.getAck();

        if (whatsappMessages != null) {

            for (WhatsappMessage whatsappMessage : whatsappMessages) {

                if (!(whatsappMessage.getChatId().matches("\\d*@c.*") && whatsappMessage.getBody().length() > 0 && whatsappMessage.getType().equals("chat"))) {
                    whatsappMessages.remove(whatsappMessage);
                    continue;
                }

                whatsappMessage.setChatId(whatsappMessage.getChatId().replaceAll("\\D", ""));
                Optional<Client> client = clientService.getClientByPhoneNumber(whatsappMessage.getChatId());

                if (!client.isPresent()) {
                    Client newClient = new Client(whatsappMessage.getSenderName(), whatsappMessage.getChatId(), whatsappMessage.getTime());
                    checkSocialProfile(whatsappMessage, newClient);
                    statusService.getFirstStatusForClient().ifPresent(newClient::setStatus);
                    newClient.addHistory(new ClientHistory("Был добавлен из WhatsApp", whatsappMessage.getTime(), ClientHistory.Type.SOCIAL_REQUEST));
                    clientService.addClient(newClient);
                    sendNotificationService.sendNewClientNotification(newClient, "whatsapp");
                    checkSocialProfile(whatsappMessage, newClient);
                    whatsappMessage.setClient(newClient);
                } else {
                    checkSocialProfile(whatsappMessage, client.get());
                    whatsappMessage.setClient(client.get());
                }
            }


            whatsappMessageService.saveAll(whatsappMessages);
        }
        if (receipts != null) {

            for (WhatsappAcknowledgement whatsappAcknowledgement : receipts) {
                Optional<WhatsappMessage> whatsappMessage = whatsappMessageService.findById(whatsappAcknowledgement.getId());
                if (whatsappMessage.isPresent() && whatsappAcknowledgement.getStatus().getPriority() > 2) {
                    whatsappMessage.get().setSeen(true);
                    whatsappMessageService.save(whatsappMessage.get());
                }
            }
        }
        return HttpStatus.OK;

    }

    private void checkSocialProfile(WhatsappMessage whatsappMessage, Client client) {
        List<SocialProfile> socialProfiles = client.getSocialProfiles();
        if (socialProfileTypeService.getByTypeName("whatsapp").isPresent()) {
            SocialProfile socialProfile = new SocialProfile(whatsappMessage.getChatId(), socialProfileTypeService.getByTypeName("whatsapp").get());
            if (client.getId() != null) {
                Optional<SocialProfile> socialProfileWhatsApp = socialProfileService.getSocialProfileByClientIdAndTypeName(client.getId(), "whatsapp");
                if (socialProfiles.contains(socialProfileWhatsApp.get())) {
                    return;
                }
            }
            socialProfiles.add(socialProfile);
            client.setSocialProfiles(socialProfiles);
        }
    }

}
