package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/telegram")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class TelegramRestController {

    private final TelegramService telegramService;
    private final ClientService clientService;

    @Autowired
    public TelegramRestController(TelegramService telegramService, ClientService clientService) {
        this.telegramService = telegramService;
        this.clientService = clientService;
    }

    @GetMapping("/phone-code")
    public HttpStatus sendAuthPhone(@RequestParam("phone") String phone) {
        telegramService.sendAuthPhone(phone);
        return HttpStatus.OK;
    }

    @GetMapping("/sms-code")
    public HttpStatus sendAuthCodeFromSms(@RequestParam("code") String code) {
        telegramService.sentAuthCode(code);
        return HttpStatus.OK;
    }

    @GetMapping("/messages/chat")
    public HttpStatus getChatMessages(@RequestParam("clientId") Long clientId) {
        List<SocialProfile> profiles =  clientService.getClientByID(clientId).getSocialProfiles();
        String chatId = "";
        for (SocialProfile profile : profiles) {
            if("telegram".equals(profile.getSocialProfileType().getName())) {
                chatId = profile.getLink();
                break;
            }
        }
        telegramService.getChatMessages(Long.parseLong(chatId));
        return HttpStatus.OK;
    }

    @GetMapping("/logout")
    public HttpStatus logoutFromTelegram() {
        telegramService.logout();
        return HttpStatus.OK;
    }
}
